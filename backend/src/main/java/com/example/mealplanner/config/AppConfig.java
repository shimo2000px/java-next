package com.example.mealplanner.config;

// 役割: SecurityConfig から独立した汎用 Bean 定義クラス。
//       PasswordEncoder をここに置くことで循環依存を断ち切る。
//
// 循環の構造:
//   SecurityConfig → JwtAuthFilter → AuthService → PasswordEncoder → (SecurityConfig に戻る)
// 解決策:
//   PasswordEncoder の定義元を SecurityConfig から AppConfig に移動する。

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
