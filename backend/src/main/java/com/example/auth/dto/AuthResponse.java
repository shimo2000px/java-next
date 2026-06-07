package com.example.auth.dto;

// 役割: 全認証APIのレスポンスボディ。仕様に従い { "message": "..." } 形式に統一。

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
}
