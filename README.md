# こどちょ

**こどちょ**は、子どもの貯蓄情報を家族で管理し、入出金の記録、残高の可視化、目標達成状況の確認、家族共有までを一つの画面体験で扱える Web アプリケーションです。[1] 実装は **Spring Boot 3.x / Java 21 / Thymeleaf / PostgreSQL / Docker Compose** を中心に構成されており、ローカル環境での立ち上げと継続開発のしやすさが意識されています。[2] [3] [4]

本リポジトリは、親アカウントを中心に複数の子どもを管理しながら、パートナーや祖父母とも情報共有できる家計・教育資金管理アプリの初期実装です。[1] 画面設計、要件、デザイン方針、アプリケーション構成がリポジトリ内のドキュメントとして整理されているため、プロダクト理解から実装参加までを比較的スムーズに進められます。[1] [5] [6]

## プロジェクト概要

本アプリの目的は、**子どものお金の記録を単なる台帳にせず、家族で成長を見守る体験へ変えること**です。要件定義では、複数の子どもの入出金記録、残高表示、目標達成率、推移グラフ、家族招待機能が中核機能として定義されています。[1] 実装上も、ダッシュボード、子ども管理、取引登録、目標設定、家族共有といった主要なユースケースがコントローラー単位で構成されています。[7] [8] [9] [10]

| 項目 | 内容 |
|---|---|
| プロダクト名 | こどちょ |
| 主な利用者 | 親、パートナー、祖父母、子ども[1] |
| 提供価値 | 家族単位での貯蓄管理、目標の可視化、共有体験の提供[1] |
| アーキテクチャ | Spring Boot による SSR 構成、Thymeleaf テンプレート、PostgreSQL 永続化[2] [4] |
| 実行方式 | Docker Compose によるアプリ・DB 同時起動[3] |

## 主な機能

こどちょでは、家族全体の状況を把握するダッシュボードと、子どもごとの詳細管理を両立しています。ダッシュボードでは家族全体の合計残高、月次増減率、月次推移グラフ、最近の履歴を表示し、子どもごとの目標進捗もまとめて確認できます。[1] [7] 子ども詳細では、個別残高、入出金履歴、月別絞り込み、目標達成率、推移グラフを扱えるため、日々の記録と長期的な目標の両方を一貫して管理できます。[1] [8] [9]

| 機能カテゴリ | 内容 | 主な実装根拠 |
|---|---|---|
| 認証 | メールアドレス + パスワード認証、および Google OAuth2 ログインに対応 | [1] [4] [11] |
| ダッシュボード | 家族全体の合計残高、月次増減率、棒グラフ、最近の履歴を表示 | [1] [7] |
| 子ども管理 | 子どもの登録、編集、削除、一覧表示 | [1] [8] |
| 入出金管理 | 子どもごとの取引登録、履歴表示、削除、月別絞り込み | [1] [9] |
| 目標管理 | 目標の登録、編集、削除、達成率表示 | [1] [10] |
| 可視化 | 月次推移グラフ、目標達成率の表示 | [1] [7] [9] |
| 家族共有 | 招待 URL 発行、承諾、再送、共有解除 | [1] [12] |
| 権限制御 | PARENT / PARTNER / GRANDPARENT / CHILD のロールベース制御 | [1] [11] |

## 技術スタック

アプリケーションは Java / Spring Boot を中核にした構成で、画面描画には Thymeleaf を採用しています。フロントエンドは SSR ベースでありながら、Chart.js によるグラフ描画を組み合わせることで、家族向けのわかりやすい可視化を実現する設計です。[1] [7] [9]

| レイヤー | 採用技術 |
|---|---|
| バックエンド | Java 21, Spring Boot 3.2.3[2] |
| Web / MVC | Spring Web, Thymeleaf[2] |
| 認証 / 認可 | Spring Security, OAuth2 Client, BCrypt[2] [11] |
| データアクセス | Spring Data JPA, Flyway[2] [4] |
| データベース | PostgreSQL 15[1] [3] |
| フロントエンド補助 | Tailwind CSS（CDN）, Chart.js（CDN）[1] |
| コンテナ | Docker, Docker Compose[1] [3] |
| ビルド | Maven[2] |

## 画面と利用フロー

本リポジトリでは、認証後にダッシュボードまたは家族画面を起点として、子どもの情報、取引、目標、共有設定へ遷移する構成になっています。[1] [5] そのため、利用者視点では「家族全体を見る」「子ども単位で管理する」「必要に応じて共有する」という導線が自然につながるように設計されています。[1] [5]

| 画面 | URL の例 | 概要 |
|---|---|---|
| ログイン | `/login` | メール認証および Google ログイン入口[11] |
| 新規登録 | `/register` | 親アカウントの作成[11] |
| ダッシュボード | `/dashboard` | 家族全体の残高・推移・最近の履歴を表示[7] |
| 家族一覧 | `/family` | 子ども一覧の表示と管理[8] |
| 子ども詳細 | `/children/{id}` | 残高、履歴、目標、グラフの確認[9] |
| 取引登録 | `/children/{id}/transactions/new` | 入金・出金の登録[9] |
| 目標登録 | `/children/{id}/goals/new` | 目標の作成[10] |
| 家族共有 | `/family/sharing` | 招待 URL の発行と共有管理[12] |

## ディレクトリ構成

README を初めて読む開発者が把握しやすいよう、主要ディレクトリのみを抜粋して示します。詳細な設計資料は `Docs/` 配下にまとめられており、実装コードは `src/main/java` と `src/main/resources` に整理されています。[5] [6]

```text
.
├── Docs/                          # 要件定義、画面仕様、構成図など
├── src/
│   ├── main/
│   │   ├── java/com/example/kodoucho/
│   │   │   ├── controller/        # 画面遷移・フォーム処理
│   │   │   ├── service/           # 業務ロジック
│   │   │   ├── entity/            # JPA エンティティ
│   │   │   └── security/          # 認証・認可設定
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf テンプレート
│   │       ├── db/migration/      # Flyway マイグレーション
│   │       └── application.yml    # アプリ設定
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## セットアップ手順

ローカルでの起動は Docker Compose を前提とすると最も簡単です。`db` と `app` の 2 サービスが定義されており、`db` の起動完了を待ってからアプリケーションが立ち上がる構成になっています。[3] また、アプリケーション側では Flyway によるマイグレーションが有効であり、起動時にスキーマ検証が行われます。[4]

### 前提条件

| 項目 | バージョン / 条件 |
|---|---|
| Docker | 利用可能であること |
| Docker Compose | 利用可能であること |
| Google OAuth 設定 | Google ログインを使う場合に必要[4] |

### 1. 環境変数を準備する

`.env.example` には Google OAuth のクライアントシークレット例が含まれています。`docker-compose.yml` では `GOOGLE_CLIENT_ID` と `GOOGLE_CLIENT_SECRET` が参照されるため、必要に応じて `.env` を作成して値を設定してください。[3] [13]

```bash
cp .env.example .env
```

`.env` の設定例は次のとおりです。

```env
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

この設定は、Google OAuth2 ログインを有効化するために利用されます。未設定時でもデフォルト値 `dummy` で起動は試みられますが、実際の Google ログインは機能しません。[3] [4]

### 2. アプリケーションを起動する

```bash
docker compose up --build
```

起動後、アプリケーションは `http://localhost:8080` で利用できます。[3] [4] PostgreSQL は `localhost:5432` に公開され、永続化には Docker ボリューム `postgres_data` が使われます。[3]

### 3. 停止する

```bash
docker compose down
```

ボリュームも含めて削除したい場合は、必要に応じて次のように実行してください。

```bash
docker compose down -v
```

## 設定値

アプリケーションの主要設定は `application.yml` と `docker-compose.yml` に集約されています。[3] [4] ローカル実行時とコンテナ実行時で接続先 DB が切り替えられるよう、環境変数による上書きが可能です。[4]

| 変数名 | 用途 | 既定値 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL 接続 URL | `jdbc:postgresql://localhost:5432/kodoucho`[4] |
| `SPRING_DATASOURCE_USERNAME` | DB ユーザー名 | `postgres`[4] |
| `SPRING_DATASOURCE_PASSWORD` | DB パスワード | `password`[4] |
| `GOOGLE_CLIENT_ID` | Google OAuth クライアント ID | `dummy`[3] [4] |
| `GOOGLE_CLIENT_SECRET` | Google OAuth クライアントシークレット | `dummy`[3] [4] |
| `server.port` | アプリ待受ポート | `8080`[4] |

## 認証と権限制御

本アプリでは、ロールベースのアクセス制御が前提になっています。要件定義では **PARENT / PARTNER / GRANDPARENT / CHILD** の 4 ロールが定義されており、親が最も広い管理権限を持ち、子どもは自分に紐づく情報の閲覧に限定されます。[1] セキュリティ設定でも、子ども管理や家族共有の URL は親権限向けに制限され、その他の画面は認証必須として扱われます。[11]

| ロール | 代表的な権限 |
|---|---|
| PARENT | 子どもの登録・編集・削除、入出金、目標管理、家族招待[1] |
| PARTNER | 招待設定に応じて編集または閲覧[1] |
| GRANDPARENT | 閲覧中心[1] |
| CHILD | 自分のデータ閲覧中心[1] [9] |

## 開発メモ

このプロジェクトは、ドメイン理解と画面理解が開発効率に直結します。まず `Docs/要件定義書.md` で機能の全体像を把握し、その後 `Docs/画面仕様書.md` と `DESIGN.md` を参照しながら UI 実装や改善を進めると、意図と実装のずれを減らしやすくなります。[1] [5] [6]

また、バックエンドの主要な振る舞いを追う際は、`controller` から `service`、必要に応じて `db/migration` を読む流れが適しています。ダッシュボード、子ども管理、取引、目標、家族共有の責務が比較的明確に分かれているため、機能追加時も影響範囲を整理しやすい構成です。[7] [8] [9] [10] [12]

## 今後の拡張候補

要件定義では、バッジ・実績機能、レポート画面、リマインダー、シェア、自動積立、CSV エクスポート、バックアップなどが将来対応として整理されています。[1] 現時点の README では、これらを「未実装だが構想済みのスコープ」として理解しておくと、ロードマップの見通しを持ちながら開発できます。[1]

| 区分 | 内容 |
|---|---|
| 分析強化 | レポート、CSV エクスポート[1] |
| 継続支援 | リマインダー、自動積立[1] |
| 家族体験 | シェア、バッジ・実績[1] |
| 運用性 | バックアップ、リストア[1] |

## References

[1]: ./Docs/%E8%A6%81%E4%BB%B6%E5%AE%9A%E7%BE%A9%E6%9B%B8.md "要件定義書"
[2]: ./pom.xml "pom.xml"
[3]: ./docker-compose.yml "docker-compose.yml"
[4]: ./src/main/resources/application.yml "application.yml"
[5]: ./Docs/%E7%94%BB%E9%9D%A2%E4%BB%95%E6%A7%98%E6%9B%B8.md "画面仕様書"
[6]: ./DESIGN.md "DESIGN.md"
[7]: ./src/main/java/com/example/kodoucho/controller/DashboardController.java "DashboardController.java"
[8]: ./src/main/java/com/example/kodoucho/controller/ChildController.java "ChildController.java"
[9]: ./src/main/java/com/example/kodoucho/controller/TransactionController.java "TransactionController.java"
[10]: ./src/main/java/com/example/kodoucho/controller/GoalController.java "GoalController.java"
[11]: ./src/main/java/com/example/kodoucho/security/SecurityConfig.java "SecurityConfig.java"
[12]: ./src/main/java/com/example/kodoucho/controller/FamilySharingController.java "FamilySharingController.java"
[13]: ./.env.example ".env.example"
