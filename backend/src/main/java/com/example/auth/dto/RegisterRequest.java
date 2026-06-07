package com.example.auth.dto;

// 役割: POST /api/auth/register のリクエストボディ。@Validと組み合わせてバリデーションを自動実行する。

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    // 8文字以上・英大小文字・数字・記号を各1文字以上
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
        message = "パスワードは8文字以上で英大小文字・数字・記号を含む必要があります"
    )
    private String password;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
}
