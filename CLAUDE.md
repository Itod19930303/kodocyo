# CLAUDE.md

このファイルはAIエージェント（Claude）がこのリポジトリで作業する際の指示書です。

---

## プロジェクト概要

**アプリ名：** こどちょ
**概要：** 子供の貯蓄情報を家族で管理するWebアプリケーション
**デザインコンセプト：** "The Gentle Guardian / The Digital Heirloom"
**ロゴ：** Material Symbols `savings`（🐷貯金箱）アイコン＋「こどちょ」テキスト（primary色）

---

## 技術スタック

| レイヤー | 技術 |
|---|---|
| バックエンド | Spring Boot 3.x（Java 21） |
| テンプレートエンジン | Thymeleaf |
| CSSフレームワーク | Tailwind CSS（CDN） |
| グラフ | Chart.js（CDN） |
| 認証 | Spring Security + Google OAuth2.0 |
| データベース | PostgreSQL 15 |
| コンテナ | Docker / Docker Compose |
| ビルドツール | Maven |

---

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

### データベース
| artifactId | 用途 |
|---|---|
| `postgresql` | PostgreSQL JDBCドライバー |
| `flyway-core` | DBマイグレーション管理 |

### ユーティリティ
| artifactId | 用途 |
|---|---|
| `lombok` | `@Getter` / `@Builder` 等 |

### テスト
| artifactId | 用途 |
|---|---|
| `spring-boot-starter-test` | JUnit5 / Mockito / MockMvc |
| `spring-security-test` | Spring Security テスト |

### フロントエンド（CDN）
| ライブラリ | 用途 | URL |
|---|---|---|
| Tailwind CSS | スタイリング | `https://cdn.tailwindcss.com` |
| Chart.js | 折れ線・棒グラフ | `https://cdn.jsdelivr.net/npm/chart.js` |
| Material Symbols | アイコン | Google Fonts CDN |
| Plus Jakarta Sans / Manrope / Noto Sans JP | フォント | Google Fonts CDN |

---

## ローカル起動方法

```bash
cp .env.example .env
# .env に GOOGLE_CLIENT_ID と GOOGLE_CLIENT_SECRET を設定

docker compose up -d
open http://localhost:8080

docker compose down
docker compose down -v  # 完全リセット
```

---

## ディレクトリ構成

```
.
├── docker-compose.yml
├── Dockerfile
├── .env.example
├── pom.xml
├── DESIGN.md
├── src/main/java/com/example/kodoucho/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── security/
└── src/main/resources/
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

## ロールと権限

| ロール | ログイン後リダイレクト | 主な権限 |
|---|---|---|
| PARENT | `/dashboard` | 全操作 |
| PARTNER（EDIT） | `/dashboard` | 入出金・目標の編集可 |
| PARTNER（VIEW） | `/dashboard` | 閲覧のみ |
| GRANDPARENT | `/dashboard` | 閲覧のみ |
| CHILD | `/children/{自分のid}` | 閲覧のみ |

---

## デザインシステム（DESIGN.md準拠）

### カラーパレット（主要）
| 変数名 | カラーコード | 用途 |
|---|---|---|
| primary | #0060ad | メインカラー・ボタン・ロゴ |
| on-primary | #f8f8ff | primaryの上のテキスト |
| surface | #f8f9ff | ページ背景（Level 0） |
| surface-container-low | #eff3ff | セクション背景（Level 1） |
| surface-container-lowest | #ffffff | カード背景（Level 2） |
| surface-container-highest | #d4e3ff | ログイン左パネル背景 |
| on-surface | #173355 | 標準テキスト（黒の代わり） |
| secondary-container | #b1fde6 | 貯蓄増加・バッジ |
| tertiary-container | #feb246 | 達成バッジ・ウォームアクセント |
| error | #ac3434 | 出金・エラー |

### デザインルール
- **No-Line Rule：** 1px borderによるセクション区切り禁止
- **角丸：** 最小 `0.5rem`。シャープな角禁止
- **純黒禁止：** `#000000` 禁止 → `on-surface (#173355)` を使用
- **ボタン：** 必ずpill形状（`rounded-full`）
- **入力フィールド：** borderなし・`surface-container-low` 背景・`rounded-xl`
- **カード：** `surface-container-lowest` + ambient shadow

---

## コーディング規約

### Java
- クラス名：UpperCamelCase / メソッド名：lowerCamelCase
- インデント：スペース4つ
- Service層にビジネスロジックを集約。Controllerは薄く保つ

### Security
- パスワードはBCryptでハッシュ化（平文保存禁止）
- CSRFトークンは有効のまま維持
- URLレベルのアクセス制御は `SecurityConfig.java` に集約
- Thymeleafでのロール制御：`sec:authorize="hasRole('PARENT')"` を使用

### Chart.js
- ダッシュボード：棒グラフ（月次入金推移）
- 子供詳細：折れ線グラフ（貯蓄推移・6ヶ月/1年切り替え）
- グラフの色はCSS変数から取得

---

## ドメインルール

1. **残高計算：** `残高 = 全入金合計 - 全出金合計`（DBに残高カラムを持たない）
2. **家族純資産：** 全childrenの残高合計
3. **目標達成判定：** `残高 >= 目標金額`（DBに達成フラグを持たない）
4. **入金カテゴリ：** `allowance` / `earned` / `new_year` / `other`（出金はNULL）
5. **学年ラベル：** birth_dateから動的に算出（乳幼児/幼稚園/小学生/中学生/高校生）
6. **データ分離：** PARENTは自分が登録した子供のデータのみ操作可能
7. **子供削除時：** ON DELETE CASCADEでtransactions・goalsを全削除

---

## 画面一覧

| 画面ID | URL | 概要 | ロール |
|---|---|---|---|
| SCR-001 | `/login` | ログイン | 未認証 |
| SCR-002 | `/register` | 新規登録（親） | 未認証 |
| SCR-003 | `/dashboard` | ダッシュボード | PARENT/PARTNER/GP |
| SCR-004 | `/family` | Family Members | PARENT/PARTNER/GP |
| SCR-005 | `/children/{id}` | 子供詳細 | 全ロール |
| SCR-006 | `/children/{id}/transactions/new` | 入出金登録 | PARENT/PARTNER(EDIT) |
| SCR-007 | `/children/new` etc. | 子供登録・編集 | PARENT |
| SCR-008 | `/children/{id}/goals/new` etc. | 目標登録・編集 | PARENT/PARTNER(EDIT) |
| SCR-009 | `/family/sharing` | 家族情報連携 | PARENT |

---

## 環境変数

| 変数名 | 説明 |
|---|---|
| GOOGLE_CLIENT_ID | Google OAuth クライアントID |
| GOOGLE_CLIENT_SECRET | Google OAuth クライアントシークレット |
| SPRING_DATASOURCE_URL | DB接続URL |
| SPRING_DATASOURCE_USERNAME | DBユーザー名 |
| SPRING_DATASOURCE_PASSWORD | DBパスワード |

---

## 実装開始ガイド

### よくある質問への回答

**Q. DESIGN.md が見つからない**
→ リポジトリルートに `DESIGN.md` が存在します。デザイン実装時は必ず参照してください。

**Q. パッケージ名は何を使う？**
→ `com.example.kodoucho` を使用してください。`com.example.shinraisavings` は古い記載です。無視してください。

**Q. コードファイルがゼロの状態から始める場合は？**
→ 以下の実装順序で進めてください。

### 推奨実装順序

**Phase 1：プロジェクト基盤**
1. `pom.xml` / `Dockerfile` / `docker-compose.yml` の作成
2. `application.yml` の設定（DB接続・Spring Security・OAuth2）
3. Flyway マイグレーションファイル（`V1__create_users.sql` 〜 `V5__create_family_shares.sql`）
4. Entity クラス（User / Child / Transaction / Goal / FamilyShare）

**Phase 2：認証**
5. `SecurityConfig.java` / `CustomUserDetailsService.java` / `OAuth2UserService.java`
6. `AuthController` + `login.html` + `register.html`（SCR-001・002）

**Phase 3：コア機能**
7. `ChildController` + Service + Repository + `form.html`（SCR-007）
8. `TransactionController` + Service + Repository + `form.html`（SCR-006）
9. Family Members画面（SCR-004）

**Phase 4：詳細・グラフ**
10. 子供詳細画面 + Chart.js グラフ（SCR-005）
11. `GoalController` + Service + `form.html`（SCR-008）

**Phase 5：ダッシュボード・家族連携**
12. `DashboardController` + `dashboard.html`（SCR-003）
13. `FamilySharingController` + `sharing.html`（SCR-009）

---

## 作業時の注意事項

- `main` ブランチへの直接pushは禁止。featureブランチを切ること
- DBスキーマ変更時はFlywayマイグレーションファイルを追加（`V{番号}__{説明}.sql`）
- `.env` は `.gitignore` に追加し、リポジトリにコミットしないこと
- デザインはDESIGN.mdのルールに厳格に従うこと
- バリデーションは `@Valid` + `BindingResult` を使用すること
