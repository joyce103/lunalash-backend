# lunalash-backend

Spring Boot (4.0.0) 後端專案，Java 17。

## Project Setup

```sh
mvn clean package
```

### Run

```sh
mvn spring-boot:run
```

## 資安更新記錄 (2026-10)

因應 2026 年 10 月公布的資安漏洞，進行以下修復：

- **Dockerfile**：build 階段 `maven:3.9.6-eclipse-temurin-17` 升級為 `maven:3.9.9-eclipse-temurin-17`；執行階段 `eclipse-temurin:17-jre-jammy` 升級為 `eclipse-temurin:17.0.20_8-jre-jammy`，對應 Oracle 2026/10 Critical Patch Update 同期的 OpenJDK 17 安全修補版本。
- **pom.xml**：新增 `dependencyManagement`，將 `cloudinary-http44` 帶入的 `httpclient` / `httpmime` 強制鎖定到 `4.5.14`，修補 `httpclient 4.4` 存在的 **CVE-2020-13956**（惡意 URI authority 導致連到錯誤主機）。
- **jjwt**：由 `0.11.5` 升級到 `0.12.6`，並改寫 `JwtUtils.java` 為新版 API（`Jwts.builder().subject()/.issuedAt()/.expiration()`、`Jwts.parser().verifyWith()/.parseSignedClaims()`），解決與 **CVE-2024-31033**（官方認定為爭議案例）相關疑慮，同時移除舊版反射機制在 JDK 17+ 下的 JPMS 問題。

> 重新部署時請務必 `docker compose build --no-cache` 以確保拉到上述修補過的映像檔版本。

### 已知、尚未處理的項目

- `cloudinary-http44` 套件本身綑綁舊版 httpclient 架構，長期應評估改用官方建議的 `cloudinary-http5`。
