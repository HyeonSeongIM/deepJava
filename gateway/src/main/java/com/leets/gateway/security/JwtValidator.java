package com.leets.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtValidator {

    private final PublicKey publicKey;

    public JwtValidator(@Value("${jwt.public-key}") Resource publicKeyResource) throws Exception {
        String pem = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String base64 = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        this.publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .clockSkewSeconds(30)   // 두 서버 시간차 대응
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}