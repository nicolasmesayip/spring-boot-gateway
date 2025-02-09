package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private final UserAuthenticationRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserAuthentication user = userRepository.findById(s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getHashedPassword(), Collections.emptyList());
    }
}
