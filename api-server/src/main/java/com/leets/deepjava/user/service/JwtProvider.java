package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final PrivateKey privateKey;
    private final long expirationMs;

    public JwtProvider(
            @Value("${jwt.private-key}") Resource privateKeyResource,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs) throws Exception {
        String pem = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String base64 = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
        this.expirationMs = expirationMs;
    }

    public String generate(Long userId, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(privateKey)
                .compact();
    }
}
