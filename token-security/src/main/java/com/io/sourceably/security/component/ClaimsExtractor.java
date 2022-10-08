package com.io.sourceably.security.component;


import io.jsonwebtoken.Claims;

public interface ClaimsExtractor {

    Claims extract(String token);

}
