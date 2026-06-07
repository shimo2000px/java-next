package com.example.auth.config;

// 役割: application.yml の app.jwt.* をJavaオブジェクトにバインドする設定クラス。
//       @ConfigurationProperties を使うことで型安全に環境変数を取得できる。

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {
    private String secret;
    private long expirationMs;
}
