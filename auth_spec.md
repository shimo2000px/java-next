# 認証機能 実装仕様書 v2
## Spring Boot × Next.js × Nginx × Docker Compose

---

## 技術スタック・バージョン一覧

| サービス | イメージ / バージョン | 備考 |
|---|---|---|
| Nginx | nginx:1.25-alpine | リバースプロキシ |
| Next.js | node:20-alpine | App Router使用 |
| Spring Boot | eclipse-temurin:17-jdk-alpine **または** eclipse-temurin:21-jdk-alpine | ※後述のバージョン分岐を参照 |
| PostgreSQL | postgres:15-alpine | |
| Java | 17 または 21 | Spring Boot 2.x→Java17、3.x→Java17/21どちらも可 |

### Spring Bootバージョン分岐（確認できるまで両対応）

```
Spring Boot 2.7.x → Java 17 / Spring Security 5.x
Spring Boot 3.2.x → Java 17 or 21 / Spring Security 6.x
```

SecurityConfigの書き方が異なるため、コード内にコメントで両バージョンを併記すること（後述）。

---

## ディレクトリ構成

```
project-root/
├── docker-compose.yml
├── nginx/
│   └── default.conf           # Nginxリバースプロキシ設定
├── backend/                   # Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/auth/
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   └── JwtConfig.java
│       ├── controller/
│       │   └── AuthController.java
│       ├── service/
│       │   └── AuthService.java
│       ├── repository/
│       │   └── UserRepository.java
│       ├── entity/
│       │   └── User.java
│       ├── dto/
│       │   ├── RegisterRequest.java
│       │   ├── LoginRequest.java
│       │   └── AuthResponse.java
│       ├── security/
│       │   ├── JwtProvider.java
│       │   └── JwtAuthFilter.java
│       └── exception/
│           └── GlobalExceptionHandler.java
└── frontend/                  # Next.js
    ├── Dockerfile
    ├── package.json
    └── src/
        ├── app/
        │   ├── (auth)/
        │   │   ├── register/page.tsx
        │   │   └── login/page.tsx
        │   └── dashboard/page.tsx
        ├── actions/
        │   └── auth.ts
        ├── middleware.ts
        └── lib/
            └── api.ts
```

---

## docker-compose.yml

```yaml
version: "3.9"

services:
  nginx:
    image: nginx:1.25-alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf:ro
    depends_on:
      - frontend
      - backend
    networks:
      - app-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    environment:
      - INTERNAL_API_URL=http://backend:8080   # コンテナ内部からSpring Bootを叩くURL
      - NEXT_PUBLIC_API_URL=http://localhost/api # ブラウザからNginx経由で叩くURL（開発確認用）
    depends_on:
      - backend
    networks:
      - app-network

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/authdb
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - APP_JWT_SECRET=your-256-bit-secret-key-here-min-32-chars!!
      - APP_JWT_EXPIRATION_MS=86400000
    depends_on:
      db:
        condition: service_healthy
    networks:
      - app-network

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=authdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - app-network

volumes:
  postgres_data:

networks:
  app-network:
    driver: bridge
```

---

## nginx/default.conf

```nginx
server {
    listen 80;

    # /api/* → Spring Boot（コンテナ名で名前解決）
    location /api/ {
        proxy_pass         http://backend:8080;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }

    # それ以外 → Next.js
    location / {
        proxy_pass         http://frontend:3000;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;
        proxy_set_header   Upgrade           $http_upgrade;
        proxy_set_header   Connection        "upgrade";  # HMR(WebSocket)対応
    }
}
```

**Nginxがリバースプロキシとして担う役割:**
- ブラウザからは `:80` の1ポートだけ公開
- URLパスで振り分け（`/api/` → Spring Boot、それ以外 → Next.js）
- コンテナ間はDocker内部ネットワーク（`app-network`）で通信
- 外からは各コンテナのポートが見えない（セキュリティ向上）

---

## backend/Dockerfile

```dockerfile
# Spring Boot 3.x 想定（2.xでも同じ手順）
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw -q package -DskipTests   # mvnwがない場合は mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## frontend/Dockerfile

```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
```

> `standalone`出力を使うため、`next.config.js` に `output: 'standalone'` を追記すること。

---

## データベース設計

```sql
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## バックエンド APIエンドポイント仕様

### POST /api/auth/register（新規登録）

**リクエスト**
```json
{ "email": "user@example.com", "password": "Password1!", "name": "山田太郎" }
```

**バリデーション**
- `email`: 必須・メール形式・255文字以内
- `password`: 必須・8文字以上・英大小文字・数字・記号を各1文字以上
- `name`: 必須・1〜100文字

**成功 201**
```json
{ "message": "登録が完了しました" }
```

**重複 409**
```json
{ "message": "このメールアドレスは既に使用されています" }
```

---

### POST /api/auth/login（ログイン）

**リクエスト**
```json
{ "email": "user@example.com", "password": "Password1!" }
```

**成功時の動作**
1. emailでユーザー取得
2. BCryptでパスワード照合
3. JWTを生成（有効期限24時間）
4. `Set-Cookie`でJWTを返す

**Set-Cookieヘッダー**
```
Set-Cookie: token=<JWT>; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=86400
```

**成功 200**
```json
{ "message": "ログインしました" }
```

**認証失敗 401**
```json
{ "message": "メールアドレスまたはパスワードが正しくありません" }
```

---

### POST /api/auth/logout

**成功時の動作**: Cookieを空・期限切れで上書き

```
Set-Cookie: token=; HttpOnly; Secure; SameSite=Lax; Path=/; Max-Age=0
```

**成功 200**
```json
{ "message": "ログアウトしました" }
```

---

## JWT仕様

| 項目 | 値 |
|---|---|
| アルゴリズム | HS256 |
| 有効期限 | 24時間 |
| クレーム | `sub`(email), `role`, `userId` |
| 秘密鍵 | 環境変数 `APP_JWT_SECRET` から取得（256bit以上） |
| ライブラリ | jjwt 0.12.x |

---

## Spring Security設定の要件

```java
// ========================================
// Spring Boot 3.x (Spring Security 6.x)
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
// Spring Boot 2.7.x (Spring Security 5.x)
// ※ WebSecurityConfigurerAdapterを継承する旧スタイル
// ========================================
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors().and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated().and()
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
}
```

**CORS設定のポイント**
- `allowedOrigins`: `http://localhost`（Nginx経由のURL）のみ許可
- `allowCredentials(true)` の場合、`allowedOrigins` にワイルドカード`*`は使えない

---

## エラーハンドリング方針

| ケース | HTTPステータス |
|---|---|
| バリデーション失敗 | 400 Bad Request |
| 認証失敗 | 401 Unauthorized |
| 権限なし | 403 Forbidden |
| メール重複 | 409 Conflict |
| サーバーエラー | 500 Internal Server Error |

レスポンスは常に `{ "message": "..." }` 形式に統一。

---

## Next.js設定

### next.config.js

```js
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',  // Dockerビルド用
}
module.exports = nextConfig
```

### 環境変数

```env
# .env.local（ローカル開発用）
INTERNAL_API_URL=http://localhost:8080   # ローカル直接
NEXT_PUBLIC_API_URL=http://localhost/api # Nginx経由（参照用）
```

### middleware.ts の要件

- `/dashboard/**` へのアクセス時にCookie内JWTを検証
- 未認証の場合は `/login` へリダイレクト

### Server Actions（actions/auth.ts）の要件

- `register(formData)`: `/api/auth/register` 呼び出し
- `login(formData)`: `/api/auth/login` 呼び出し。レスポンスの`Set-Cookie`をNext.js側のCookieに転送
- `logout()`: `/api/auth/logout` 呼び出し後、Cookieを削除して `/login` へリダイレクト

**コンテナ内部からAPIを叩く場合のURL:**
```
Server Actions内 → http://backend:8080/api/auth/login  （Docker内部通信）
ブラウザから直接 → http://localhost/api/auth/login     （Nginx経由）
```

---

## application.yml（Spring Boot）

```yaml
app:
  jwt:
    secret: ${APP_JWT_SECRET}
    expiration-ms: ${APP_JWT_EXPIRATION_MS:86400000}

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/authdb}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

server:
  port: 8080
```

---

## pom.xml 主要依存関係

```xml
<!-- Spring Boot 3.2.x の場合 -->
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.2.3</version>  <!-- 2.x の場合は 2.7.18 -->
</parent>

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
  </dependency>
  <!-- JWT (jjwt 0.12.x) -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
</dependencies>
```

---

## Claude Codeへの実装指示

以下の順序で実装すること。

1. `docker-compose.yml` と `nginx/default.conf`
2. `backend/Dockerfile` と `frontend/Dockerfile`
3. `User.java`（Entity）
4. `UserRepository.java`
5. `JwtProvider.java`
6. `SecurityConfig.java`（Spring Boot 2.x / 3.x 両バージョンをコメントで併記）
7. `JwtAuthFilter.java`
8. `AuthService.java`
9. `AuthController.java` + DTOクラス群
10. `GlobalExceptionHandler.java`
11. Next.js: `actions/auth.ts`
12. Next.js: `middleware.ts`
13. Next.js: ログイン・登録ページ（page.tsx）

**各ファイルに以下を必ずコメントで記述すること:**
- このファイルの役割（1〜2行）
- Spring Boot 2.x / 3.x で書き方が変わる箇所があれば両方を示す
- セキュリティ上の重要ポイント

**動作確認コマンド（実装後に記載すること）:**
```bash
docker compose up --build
# → http://localhost でNext.jsにアクセスできること
# → http://localhost/api/auth/register でSpring Boot APIを叩けること
```
