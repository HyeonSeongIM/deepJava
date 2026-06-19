package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() throws Exception {
        secretKey = Jwts.SIG.HS256.key().build();
        String base64Secret = Encoders.BASE64.encode(secretKey.getEncoded());
        jwtProvider = new JwtProvider(base64Secret, 3_600_000L);
    }

    @Test
    void JWT에_userId와_role이_담긴다() {
        String token = jwtProvider.generate(42L, Role.USER);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    @Test
    void 만료시간이_설정된다() {
        String token = jwtProvider.generate(1L, Role.ADMIN);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }
}
