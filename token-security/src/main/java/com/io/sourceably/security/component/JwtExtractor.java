package com.io.sourceably.security.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;


import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class JwtExtractor implements TokenExtractor {

    private final JwtSettings settings;

    @Autowired
    public JwtExtractor(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public String extract(ServletServerHttpRequest request) {
        return extract( request.getHeaders().getFirst(settings.getTokenHeader()));
    }

    @Override
    public String extract(String header) {

        if (isBlank(header) || !header.startsWith(settings.getTokenPrefix().concat(" "))) {
            return null;
        }

        return header.substring(settings.getTokenPrefix().length() + 1);
    }
}
