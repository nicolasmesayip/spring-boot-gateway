package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.common.email.EmailConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(EmailConfiguration.class)
@Import(com.nicolasmesa.springboot.usermanagement.mapper.UserAccountMapperImpl.class)
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}