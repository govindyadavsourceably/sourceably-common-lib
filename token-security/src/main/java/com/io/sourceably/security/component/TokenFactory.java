package com.io.sourceably.security.component;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface TokenFactory {


    String generate(UserDto userDto,
                    Collection<? extends GrantedAuthority> authorities);

    String generateResetPassword(UserDto userDto, Long code);

    String refresh(String token);

}
