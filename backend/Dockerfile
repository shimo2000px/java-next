# マルチステージビルド: ビルド環境と実行環境を分離してイメージを軽量化する
# amazoncorretto:17-alpine を採用 → Alpine ベースで AMD64/ARM64(Apple Silicon) 両対応
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
# 依存関係だけ先にダウンロードしてキャッシュを活用
RUN ./mvnw dependency:go-offline -q
COPY src ./src
RUN ./mvnw -q package -DskipTests

# 実行環境: JREのみで十分（JDKは不要）
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
