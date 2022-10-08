package com.io.sourceably;

import com.io.sourceably.security.component.TokenContext;
import feign.RequestInterceptor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TokenPassthroughInterceptorProvider {

    @Autowired
    private TokenContext tokenContext;

    @Bean
    public RequestInterceptor tokenPassthroughInterceptor() {
        return requestTemplate -> {
            if (tokenContext.token() != null && !requestTemplate.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                val token = tokenContext.token();
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        };
    }

}
