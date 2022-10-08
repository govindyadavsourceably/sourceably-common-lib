package com.io.sourceably.security.component;

public interface TokenVerifier {

    void verify(String payload);

}
