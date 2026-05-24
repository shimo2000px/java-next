package com.example.auth.service;

// 役割: 認証のビジネスロジック（登録・ログイン・ログアウト）を担う。
//       コントローラとリポジトリの中間層として、トランザクション管理もここで行う。

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // Spring Security がログイン時に呼び出す（UserDetailsService の実装）
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("このメールアドレスは既に使用されています");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCryptでハッシュ化
                .name(request.getName())
                .role("USER")
                .build();

        userRepository.save(user);
    }

    public void login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("メールアドレスまたはパスワードが正しくありません"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("メールアドレスまたはパスワードが正しくありません");
        }

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole(), user.getId());
        addTokenCookie(response, token, 86400); // 24時間
    }

    public void logout(HttpServletResponse response) {
        addTokenCookie(response, "", 0); // Cookie を空・期限切れで上書き
    }

    // JWT を HttpOnly Cookie にセットする
    // HttpOnly: JSからアクセス不可（XSS対策）
    private void addTokenCookie(HttpServletResponse response, String value, int maxAge) {
        Cookie cookie = new Cookie("token", value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);    // HTTPS必須（Nginx経由を想定）
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        // SameSite=Lax は Cookie コンストラクタで直接設定できないため、Headerで追加
        response.addHeader("Set-Cookie",
            String.format("token=%s; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=%d", value, maxAge));
    }

    // メール重複時の例外
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}
