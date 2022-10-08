package com.io.sourceably.security;
import com.io.sourceably.security.component.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Component
public class JwtAuthenticationFilter implements ServerSecurityContextRepository {

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private JwtAuthenticationProvider authenticationManager;

    @Override
    public Mono save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono load(ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = tokenExtractor.extract(authHeader);
        if(StringUtils.isEmpty(token))
            return Mono.empty();
        else
        return this.authenticationManager.authenticate(
                new JwtAuthentication(
                        token
                )
        ) .map((auth)->new SecurityContextImpl((Authentication) auth)).onErrorResume(Exception.class,e -> {
            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            swe.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String response="{\"message\":\"Token expired\"}";
            DataBuffer buffer=swe.getResponse().bufferFactory().wrap(response.getBytes());
            return swe.getResponse().writeWith(Flux.just(buffer));
        });


    }

}
