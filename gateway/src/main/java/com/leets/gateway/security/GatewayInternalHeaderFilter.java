package com.leets.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayInternalHeaderFilter implements GlobalFilter, Ordered {

    static final String HEADER_NAME = "X-Gateway-Secret";

    private final String internalSecret;

    public GatewayInternalHeaderFilter(@Value("${gateway.internal-secret}") String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove(HEADER_NAME);       // 클라이언트 위조 방지
                    h.set(HEADER_NAME, internalSecret);
                })
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -99;  // JWT 필터(-100) 이후 실행
    }
}
