package com.io.sourceably.security.component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtVerifier implements TokenVerifier {

    private final JwtSettings settings;

    @Autowired
    public JwtVerifier(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public void verify(String payload) {
        try {
            Jwts.parser()
                .requireIssuer(settings.getTokenIssuer())
                .setSigningKey(settings.getSecret())
                .parseClaimsJws(payload);
        } catch (IllegalArgumentException e) {
            log.debug("JWT missing error", e);
            throw new BadCredentialsException("No token");
        } catch (ExpiredJwtException e) {
            log.debug("JWT expiration error", e);
            throw new CredentialsExpiredException("Expired token");
        } catch (UnsupportedJwtException e) {
            log.error("JWT unsupported error", e);
            throw new BadCredentialsException("Invalid token");
        } catch (MalformedJwtException  e) {
            log.error("JWT malformed error", e);
            throw new BadCredentialsException("Invalid token");
        } catch (MissingClaimException  e) {
            log.error("JWT missing claim error", e);
            throw new BadCredentialsException("Invalid token");
        } catch (InvalidClaimException  e) {
            log.error("JWT invalid claim error", e);
            throw new BadCredentialsException("Invalid token");
        } catch (SignatureException e) {
            log.error("JWT signature error", e);
            throw new BadCredentialsException("Corrupted token");
        }
    }

}
