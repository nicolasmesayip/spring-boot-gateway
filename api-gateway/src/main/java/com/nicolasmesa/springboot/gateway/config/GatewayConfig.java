package com.nicolasmesa.springboot.gateway.config;

import com.nicolasmesa.springboot.gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/test")
                        .uri("http://localhost:10000"))
                .route(r -> r.path("/api/users/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("http://localhost:8081"))

                .route(r -> r.path("/api/auth/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("http://localhost:8090"))
                .build();
    }


}
