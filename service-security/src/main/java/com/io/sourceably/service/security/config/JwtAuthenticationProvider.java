package com.io.sourceably.service.security.config;


import com.io.sourceably.security.component.TokenParser;
import com.io.sourceably.security.component.TokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private TokenVerifier tokenVerifier;

    @Autowired
    private TokenParser tokenParser;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getCredentials();

        // if (token == null) return new JwtAuthentication(null);

        tokenVerifier.verify(token);

        return new JwtAuthentication(tokenParser.getSubject(token), token, tokenParser.getAuthorities(token));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
