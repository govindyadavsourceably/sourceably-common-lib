package com.io.sourceably.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken{

    private static final long serialVersionUID = 2877954820905567501L;

    @Getter
    private final Object principal;
    @Getter
    private final Object credentials;

    public JwtAuthentication(Object credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
    }

    public JwtAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

}
