# アーキテクト向け設計情報

## 設計原則

- Controller は薄く保ち、ビジネスロジックは Service に集約する
- Repository は永続化責務に限定する
- 所有者チェックと権限制御を全アクセスポイントで考慮する（`findByIdAndParent()` を必ず経由）
- N+1 クエリや不要な多重取得を避ける
- DB変更時は Flyway マイグレーションを追加する

## パッケージ名

`com.example.kodoucho`（`com.example.shinraisavings` は旧名・使用禁止）

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

詳細（依存ライブラリ・実装フェーズ）は `.claude/references/architect-reference.md` を参照。

> DESIGN.md はリポジトリルート（`DESIGN.md`）に存在する。
