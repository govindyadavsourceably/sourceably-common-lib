package com.io.sourceably.security.component;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface TokenParser {

    List<? extends GrantedAuthority> getAuthorities(String token);

    String getSubject(String token);


    String getDomain(String token);
    Long getUserId(String token);
    Long getClientId(String token);


}
