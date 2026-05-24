package com.example.auth.exception;

// 役割: アプリケーション全体の例外をキャッチし、仕様に沿った { "message": "..." } 形式に変換して返す。
//       @ControllerAdvice により全コントローラに横断的に適用される。

import com.example.auth.dto.AuthResponse;
import com.example.auth.service.AuthService.EmailAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // バリデーション失敗 → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new AuthResponse(message));
    }

    // メール重複 → 409
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(ex.getMessage()));
    }

    // 認証失敗 → 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(ex.getMessage()));
    }

    // その他のサーバーエラー → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse("サーバーエラーが発生しました"));
    }
}
