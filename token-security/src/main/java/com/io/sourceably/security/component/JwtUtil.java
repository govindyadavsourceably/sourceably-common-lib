package com.io.sourceably.security.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.Instant.now;
import static java.util.Date.from;

@Component
public class JwtUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    private String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Instant getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt).toInstant();
    }

    private Instant getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration).toInstant();
    }

    private String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    private  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Instant expiration = getExpirationDateFromToken(token);
        return false;//expiration.isBefore(now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Instant created, Instant lastPasswordReset) {
        return (lastPasswordReset != null && created.isBefore(lastPasswordReset));
    }



    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Instant createdDate = now();
        final Instant expirationDate = calculateExpirationDate(createdDate);

        System.out.println("doGenerateToken " + createdDate);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(from(createdDate))
                .setExpiration(from(expirationDate))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Instant lastPasswordReset) {
        final Instant created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        final Instant createdDate = now();
        final Instant expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(from(createdDate));
        claims.setExpiration(from(expirationDate));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        // JwtUser user = (JwtUser) userDetails;
        UserDetails user = userDetails;
        final String username = getUsernameFromToken(token);
        final Instant created = getIssuedAtDateFromToken(token);
        final Instant expiration = getExpirationDateFromToken(token);
        return (
              username.equals(user.getUsername())
                    && !isTokenExpired(token)
                    /* && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()) */
        );
    }

    private Instant calculateExpirationDate(Instant createdDate) {
        return createdDate.plusMillis(expiration * 1000);
    }
}
