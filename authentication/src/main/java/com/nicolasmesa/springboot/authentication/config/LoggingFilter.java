//package com.nicolasmesa.springboot.authentication.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class LoggingFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // Log request details before processing the request.
//        logger.info("Request: Method={}, URI={}", request.getMethod(), request.getRequestURI());
//
//        long startTime = System.currentTimeMillis();
//
//        try {
//            filterChain.doFilter(request, response);
//        } finally {
//            long duration = System.currentTimeMillis() - startTime;
//
//            // Log response details after request processing.
//            logger.info("Response: Status={}, URI={}, Duration={}ms", response.getStatus(), request.getRequestURI(), duration);
//
//            // Log any exceptions that occurred during processing.
//            if (request.getAttribute("javax.servlet.error.exception") != null) {
//                logger.error("Exception during request processing", (Exception) request.getAttribute("javax.servlet.error.exception"));
//            }
//        }
//    }
//}
