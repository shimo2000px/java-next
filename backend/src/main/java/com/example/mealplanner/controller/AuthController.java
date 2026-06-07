package com.example.mealplanner.controller;

import com.example.mealplanner.dto.AuthResponse;
import com.example.mealplanner.dto.LoginRequest;
import com.example.mealplanner.dto.RegisterRequest;
import com.example.mealplanner.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse("登録が完了しました"));
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
