package com.io.sourceably.security.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

import static java.time.Instant.now;
import static java.util.Date.from;
import static java.util.UUID.randomUUID;

@Component
public class JwtFactory implements TokenFactory {

    static final String AUDIENCE_UNKNOWN = "unknown";
    static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";

    private final JwtSettings settings;
    private final JwtVerifier verifier;

    @Autowired
    public JwtFactory(JwtSettings settings, JwtVerifier verifier) {
        this.settings = settings;
        this.verifier = verifier;
    }

    private Instant calculateExpirationDate(Instant createdDate) {
        return createdDate.plusMillis(settings.getTokenExpiration() * 1000);
    }

    private Instant calculateLinkExpirationDate(Instant createdDate) {
        return createdDate.plusMillis(settings.getLinkExpireration() * 1000);
    }

    @Override
    public String generate( UserDto userDto,
                           Collection<? extends GrantedAuthority> authorities) {


        Claims claims = Jwts.claims();
        claims.put(JwtSettings.CLAIM_SCOPES, map(authorities));
        claims.put(JwtSettings.CLAIM_USERID,userDto.getId());
        claims.put(JwtSettings.CLAIM_CLIENTID, userDto.getClientId());
        claims.put(JwtSettings.CLAIM_DOMAIN, userDto.getContext());
        Instant createdDate = now();
        Instant expirationDate = calculateExpirationDate(createdDate);

        return Jwts.builder()
                .setId(randomUUID().toString())
                .setClaims(claims)
                .setSubject(userDto.getName())
                .setExpiration(from(expirationDate))
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(from(createdDate))
                .signWith(SignatureAlgorithm.HS512, settings.getSecret())
                .compact();
    }

    @Override
    public String generateResetPassword(UserDto userDto, Long code ) {
    String   username=  userDto.getName()+"_"+code;
        Claims claims = Jwts.claims();
        claims.put(JwtSettings.CLAIM_USERID,userDto.getId());
        claims.put(JwtSettings.CLAIM_CLIENTID, userDto.getClientId());
        claims.put(JwtSettings.CLAIM_DOMAIN, userDto.getContext());

        Instant createdDate = now();
        Instant expirationDate = calculateLinkExpirationDate(createdDate);

        return Jwts.builder()
                .setId(randomUUID().toString())
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(from(expirationDate))

                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(from(createdDate))
                .signWith(SignatureAlgorithm.HS512, settings.getSecret())
                .compact();

    }

    @Override
    public String refresh(String token) {
        verifier.verify(token);

        Instant createdDate = now();
        Instant expirationDate = calculateExpirationDate(createdDate);

        Claims claims = Jwts.parser()
                .setSigningKey(settings.getSecret())
                .parseClaimsJws(token)
                .getBody();

        claims.setIssuedAt(from(createdDate));
        claims.setExpiration(from(expirationDate));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, settings.getSecret())
                .compact();
    }

    private static String[] map(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

}
