package com.nicolasmesa.springboot.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email-configuration")
public record EmailConfiguration(
        String host,
        String port,
        boolean auth,
        boolean enableTLS,
        String senderEmail,
        String senderPassword
) {
}
