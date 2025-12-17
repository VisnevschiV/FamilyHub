package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long ttlSeconds;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${app.jwt.ttl-seconds}") long ttlSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.ttlSeconds = ttlSeconds;
    }

    public String createToken(UserAccount account) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .subject(account.getEmail()) // simplest: email as "sub"
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}