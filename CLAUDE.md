# CLAUDE.md

このリポジトリでは、本ファイルを読み、変更内容に応じて必要な補助資料だけ参照すること。

## プロジェクト概要
- アプリ名: こどちょ
- 概要: 子供の貯蓄情報を家族で管理するWebアプリ
- Backend: Spring Boot 3.x / Java 21 / PostgreSQL / Maven
- Frontend: Thymeleaf / Tailwind CSS / Chart.js
- Auth: Spring Security + Google OAuth2.0

## 常時守るルール
- `main` へ直接 push しない
- DBスキーマ変更時は Flyway マイグレーションを追加する（`V{番号}__{説明}.sql`）
- `.env` はコミットしない
- Service 層にビジネスロジックを集約し、Controller は薄く保つ
- 権限チェックと所有者チェックを必ず行う
- デザインは `.claude/designer.md` のルールに従うこと

## 変更内容ごとの参照先
- 要件・権限・画面遷移: `.claude/pm.md`
- 設計・責務分離・DB方針: `.claude/architect.md`
- Java実装・Security: `.claude/coder.md`
- UI/スタイル: `.claude/designer.md`
- テスト方針: `.claude/tester.md`
- コミット・コメント・ブランチ規約: `.claude/rules.md`
- 変更規模別ワークフロー: `.claude/workflow.md`

## 作業方針
- 軽微修正ではロール別作業不要。必要な範囲だけ確認して最小修正する
- 通常・大規模変更では `.claude/workflow.md` を参照する
