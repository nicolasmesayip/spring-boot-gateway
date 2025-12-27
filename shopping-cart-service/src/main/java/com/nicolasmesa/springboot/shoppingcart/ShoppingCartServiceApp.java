package com.nicolasmesa.springboot.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShoppingCartServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartServiceApp.class, args);
    }
}
