package com.example.mealplanner.repository;

// 役割: usersテーブルへのCRUDアクセスを提供するリポジトリ。Spring Data JPAが実装を自動生成する。

import com.example.mealplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // emailでユーザーを検索（ログイン・重複チェックで使用）
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
