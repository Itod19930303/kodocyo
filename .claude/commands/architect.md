# アーキテクト向け設計情報

## 依存ライブラリ（pom.xml）

### Spring Boot Starters
| artifactId | 用途 |
|---|---|
| `spring-boot-starter-web` | Spring MVC / Webアプリ基盤 |
| `spring-boot-starter-thymeleaf` | Thymeleafテンプレートエンジン |
| `thymeleaf-extras-springsecurity6` | `sec:authorize` 使用 |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-oauth2-client` | Google OAuth2.0 |
| `spring-boot-starter-data-jpa` | Spring Data JPA |
| `spring-boot-starter-validation` | `@Valid` バリデーション |
| `postgresql` | PostgreSQL JDBCドライバー |
| `flyway-core` | DBマイグレーション管理 |
| `lombok` | `@Getter` / `@Builder` 等 |
| `spring-boot-starter-test` | JUnit5 / Mockito / MockMvc |
| `spring-security-test` | Spring Security テスト |

### フロントエンド（CDN）
| ライブラリ | 用途 |
|---|---|
| Tailwind CSS | スタイリング（`https://cdn.tailwindcss.com`） |
| Chart.js | 折れ線・棒グラフ |
| Material Symbols | アイコン（Google Fonts CDN） |
| Plus Jakarta Sans / Noto Sans JP | フォント（Google Fonts CDN） |

---

## ディレクトリ構成

```
src/main/java/com/example/kodoucho/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── security/

src/main/resources/
├── templates/
│   ├── auth/        # login.html, register.html
│   ├── dashboard.html
│   ├── home.html
│   ├── child/       # detail.html, form.html
│   ├── transaction/ # form.html
│   ├── goal/        # form.html
│   └── family/      # sharing.html
├── static/
│   ├── css/style.css
│   └── js/app.js
├── db/migration/    # Flyway V1__*.sql
└── application.yml
```

---

## 推奨実装順序

**Phase 1：プロジェクト基盤**
1. `pom.xml` / `Dockerfile` / `docker-compose.yml`
2. `application.yml`（DB接続・Spring Security・OAuth2）
3. Flyway マイグレーション（`V1__create_users.sql` 〜 `V5__create_family_shares.sql`）
4. Entity（User / Child / Transaction / Goal / FamilyShare）

**Phase 2：認証**
5. `SecurityConfig.java` / `CustomUserDetailsService.java` / `OAuth2UserService.java`
6. `AuthController` + `login.html` + `register.html`

**Phase 3：コア機能**
7. `ChildController` + Service + Repository + `form.html`
8. `TransactionController` + Service + Repository + `form.html`
9. Family Members画面

**Phase 4：詳細・グラフ**
10. 子供詳細画面 + Chart.js グラフ
11. `GoalController` + Service + `form.html`

**Phase 5：ダッシュボード・家族連携**
12. `DashboardController` + `dashboard.html`
13. `FamilySharingController` + `sharing.html`

---

## よくある質問

**Q. パッケージ名は？**
→ `com.example.kodoucho`（`com.example.shinraisavings` は旧名・使用禁止）

**Q. DESIGN.md が見つからない**
→ リポジトリルート（`D:\developWorkSpace\kodocyoApp\DESIGN.md`）に存在します。
