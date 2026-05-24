package com.example.auth.controller;

// 役割: /api/auth/* のエンドポイントを定義するRESTコントローラ。
//       バリデーション（@Valid）はここで起動し、ビジネスロジックはAuthServiceに委譲する。

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse("登録が完了しました"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        authService.login(request, response);
        return ResponseEntity.ok(new AuthResponse("ログインしました"));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok(new AuthResponse("ログアウトしました"));
    }
}
