package com.io.sourceably.security.component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtClaimsExtractor implements ClaimsExtractor {

    private final JwtSettings settings;

    @Autowired
    public JwtClaimsExtractor(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public Claims extract(String token) {
        return Jwts.parser()
                .setSigningKey(settings.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

}
