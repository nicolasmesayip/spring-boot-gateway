package com.nicolasmesa.springboot.gateway.filter;

import com.nicolasmesa.springboot.common.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final List<String> PUBLIC_ENDPOINTS = List.of("/api/auth");

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = exchange.getRequest().getURI().getPath();

        // Skip authentication for excluded paths
        boolean excluded = PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
        if (excluded) {
            return chain.filter(exchange);
        }

        if (this.isAuthMissing(request)) {
            return this.onError(exchange);
        }

        final String token = this.getToken(request);
        if (!jwtUtil.isTokenValid(token)) {
            return this.onError(exchange);
        }

        this.updateRequest(exchange, token);

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private String getToken(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey("Authorization")) return true;
        return (!request.getHeaders().getOrEmpty("Authorization").get(0).startsWith("Bearer "));
    }

    private void updateRequest(ServerWebExchange exchange, String token) {
        String email = jwtUtil.extractClaim(token, Claims::getSubject);
        exchange.getRequest().getHeaders().add("X-GATEWAY-EMAIL", email);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
