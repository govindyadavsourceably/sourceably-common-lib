package com.io.sourceably.security.component;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Component
public class JwtParser implements TokenParser {

    private final ClaimsExtractor claimsExtractor;

    private final JwtSettings settings;

    @Autowired
    public JwtParser(JwtSettings settings, ClaimsExtractor claimsExtractor) {
        this.settings = settings;
        this.claimsExtractor = claimsExtractor;
    }

   @Override
    public List<? extends GrantedAuthority> getAuthorities(String token) {
        // TODO: this is not very nice, make it pretty :)
        ArrayList<?> claim = get(token, JwtSettings.CLAIM_SCOPES, ArrayList.class).orElseGet(ArrayList::new);
        String[] scopes = claim.stream().map(o -> o.toString()).toArray(i -> new String[i]);
        return createAuthorityList(scopes);
    }

    public String getSubject(String token) {
        return getClaim(token, Claims::getSubject).get();
    }


    @Override
    public String getDomain(String token) {
        return  get(token, JwtSettings.CLAIM_DOMAIN, String.class).get();
    }

    @Override
    public Long getUserId(String token) {
          return get(token, JwtSettings.CLAIM_USERID, Long.class).get();
    }

    @Override
    public Long getClientId(String token) {
        return get(token, JwtSettings.CLAIM_CLIENTID, Long.class).get();
    }


    public <T> Optional<T> get(String token, String claim, Class<T> type) {
        return Optional.ofNullable(claimsExtractor.extract(token).get(claim, type));
    }

 private <T> Optional<T> getClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = claimsExtractor.extract(token);
        return Optional.of(resolver.apply(claims));
    }

}
