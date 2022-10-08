package com.io.sourceably.security.component;

import org.springframework.http.server.ServletServerHttpRequest;


public interface TokenExtractor {

    String extract(ServletServerHttpRequest request);

    String extract(String header);

}
