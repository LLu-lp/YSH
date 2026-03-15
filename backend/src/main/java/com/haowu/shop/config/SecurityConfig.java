package com.haowu.shop.config;

import com.haowu.shop.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 匿名访问
                .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                .requestMatchers("/api/merchant/register", "/api/merchant/login").permitAll()
                .requestMatchers("/api/admin/login").permitAll()
                .requestMatchers("/api/products", "/api/products/**", "/api/categories", "/api/banners").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                // 文件上传需要认证
                .requestMatchers("/api/upload/**").authenticated()
                // 角色级别授权
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/merchant/**").hasAnyRole("MERCHANT", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}