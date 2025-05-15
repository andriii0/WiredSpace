package org.main.wiredspaceapi.security.token.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.main.wiredspaceapi.security.token.AccessToken;
import org.main.wiredspaceapi.security.token.TokenDecoder;
import org.main.wiredspaceapi.security.token.TokenEncoder;
import org.main.wiredspaceapi.security.token.exception.InvalidAccessTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AccessTokenEncoderDecoderImpl implements TokenEncoder, TokenDecoder {
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_USER_ID = "userId";

    private final Key key;
    private final long expiration;
    private final ChronoUnit expirationUnit;


    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey
            , @Value("${jwt.access-token.exp}")long expiration
            , @Value("${jwt.access-token.exp-unit}")String expirationUnit) {
        this.expiration = expiration;
        this.expirationUnit = ChronoUnit.valueOf(expirationUnit.toUpperCase());

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
   }

    @Override
    public String encode(AccessToken token) {
        Map<String, Object> claimsMap = new HashMap<>();
        populateClaims(token, claimsMap);

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(token.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expiration, expirationUnit)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }


    @Override
    public AccessToken decode(String tokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(tokenEncoded);
            Claims claims = jwt.getBody();

            return createTokenFromClaims(claims);
        } catch (JwtException e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }

    private void populateClaims(AccessToken token, Map<String, Object> claimsMap) {
        if (token.getAccountId() != null) {
            claimsMap.put("userId", token.getAccountId().toString());
        }
        if (token.getRoles() != null && !token.getRoles().isEmpty()) {
            claimsMap.put("roles", token.getRoles());  // Список строк: ["ROLE_STANDARD_USER"]
        }
    }


    private AccessToken createTokenFromClaims(Claims claims) {
        String subject = claims.getSubject();
        UUID accountId = UUID.fromString(claims.get(CLAIM_USER_ID, String.class));

        List<String> rolesList = claims.get(CLAIM_ROLES, List.class);
        Set<String> roles = rolesList != null ? new HashSet<>(rolesList) : Set.of();

        return AccessToken.builder()
                .subject(subject)
                .accountId(accountId)
                .roles(roles)
                .build();
    }
}
