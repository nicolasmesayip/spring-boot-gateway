package com.nicolasmesa.springboot.gateway.filter;

import com.nicolasmesa.springboot.common.JwtTokenUtil;
import com.nicolasmesa.springboot.gateway.route.RouterValidator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = this.getToken(request);
            System.out.println(token);
            if (!jwtUtil.isTokenValid(token)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            this.updateRequest(exchange, token);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
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
        System.out.println("Updating header: " + email);
        exchange.getRequest().getHeaders().add("X-GATEWAY-EMAIL", email);
    }
}
