# 第一階段：使用 Maven 編譯打包
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# 先複製 pom.xml 並下載依賴 (利用 Docker 快取機制加速後續建置)
COPY pom.xml .
RUN mvn dependency:go-offline
# 複製原始碼並打包 (略過測試以加速)
COPY src ./src
RUN mvn clean package -DskipTests

# 第二階段：執行環境 (使用輕量級的 JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 把第一階段打包好的 jar 檔拿過來
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# 啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]