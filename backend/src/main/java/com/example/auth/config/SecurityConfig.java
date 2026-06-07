package com.example.auth.config;

// 役割: Spring Securityの全体設定。JWT認証フィルターの組み込み、CORS設定、パス別認可ルールを定義する。
// セキュリティポイント:
//   - セッションを使わない（STATELESS）ため CSRF 無効化は安全
//   - allowedOrigins にワイルドカード不可（allowCredentials=true 時の制約）

import com.example.auth.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // ========================================
    // Spring Boot 3.x (Spring Security 6.x) の書き方
    // ========================================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ========================================
    // Spring Boot 2.7.x (Spring Security 5.x) の書き方（参考）
    // WebSecurityConfigurerAdapter を継承するスタイル:
    //
    // @Override
    // protected void configure(HttpSecurity http) throws Exception {
    //     http
    //         .csrf().disable()
    //         .cors().and()
    //         .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    //         .authorizeRequests()
    //             .antMatchers("/api/auth/**").permitAll()
    //             .anyRequest().authenticated().and()
    //         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    // }
    // ========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Cookieを含むリクエストを許可

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
