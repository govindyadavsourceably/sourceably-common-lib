package com.io.sourceably.security;
import com.io.sourceably.security.component.TokenParser;
import com.io.sourceably.security.component.TokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationProvider implements ReactiveAuthenticationManager  {

    @Autowired
    private TokenVerifier tokenVerifier;

    @Autowired
    private TokenParser tokenParser;

    @Override
    @SuppressWarnings("unchecked")
    public Mono authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        try {
            tokenVerifier.verify(token);
        }catch (Exception e){
            return Mono.error(e);
        }
       return Mono.just(new JwtAuthentication(tokenParser.getSubject(token), token, tokenParser.getAuthorities(token)));
    }
}
