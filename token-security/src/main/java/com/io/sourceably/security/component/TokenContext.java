package com.io.sourceably.security.component;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class TokenContext {
   @Autowired
    private final TokenParser tokenParser;

    public String token() {

        return (String) Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
                .map(Authentication::getCredentials).orElse(null);
    }
    public String getUsername(){
        return tokenParser.getSubject(token());

    }
    public Long getUserId(){
        return tokenParser.getUserId(token());

    }
    public Long getClientId(){
        return tokenParser.getClientId(token());

    }
    public String getContext(){
        return tokenParser.getDomain(token());

    }
}