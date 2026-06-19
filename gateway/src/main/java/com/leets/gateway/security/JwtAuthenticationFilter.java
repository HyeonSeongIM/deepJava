package com.leets.gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;

    private static final List<String> WHITELIST = List.of(
            "/api/auth/login", "/api/auth/signup"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ① 클라이언트가 보낸 X-User-Id는 무조건 제거 (위조 방지)
        ServerHttpRequest stripped = request.mutate()
                .headers(h -> h.remove("X-User-Id"))
                .build();

        // ② 화이트리스트(로그인 등)는 검증 스킵
        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange.mutate().request(stripped).build());
        }

        // ③ 토큰 추출
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }
        String token = authHeader.substring(7);

        // ④ 검증 통과 → 검증된 값으로 헤더 주입
        try {
            Claims claims = jwtValidator.validateAndGetClaims(token);
            ServerHttpRequest mutated = stripped.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;  // 라우팅보다 먼저 실행
    }
}