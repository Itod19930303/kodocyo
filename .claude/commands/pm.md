# PM（プロダクトマネージャー）向け仕様

## ロールと権限

| ロール | ログイン後リダイレクト | 主な権限 |
|---|---|---|
| PARENT | `/dashboard` | 全操作 |
| PARTNER（EDIT） | `/dashboard` | 入出金・目標の編集可 |
| PARTNER（VIEW） | `/dashboard` | 閲覧のみ |
| GRANDPARENT | `/dashboard` | 閲覧のみ |
| CHILD | `/children/{自分のid}` | 閲覧のみ |

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

## ドメインルール

1. **残高計算：** `残高 = 全入金合計 - 全出金合計`（DBに残高カラムを持たない）
2. **家族純資産：** 全childrenの残高合計
3. **目標達成判定：** `残高 >= 目標金額`（DBに達成フラグを持たない）
4. **入金カテゴリ：** `allowance` / `earned` / `new_year` / `other`（出金はNULL）
5. **学年ラベル：** birth_dateから動的に算出（乳幼児/幼稚園/小学生/中学生/高校生）
6. **データ分離：** PARENTは自分が登録した子供のデータのみ操作可能
7. **子供削除時：** ON DELETE CASCADEでtransactions・goalsを全削除

---

## 環境変数

| 変数名 | 説明 |
|---|---|
| GOOGLE_CLIENT_ID | Google OAuth クライアントID |
| GOOGLE_CLIENT_SECRET | Google OAuth クライアントシークレット |
| SPRING_DATASOURCE_URL | DB接続URL |
| SPRING_DATASOURCE_USERNAME | DBユーザー名 |
| SPRING_DATASOURCE_PASSWORD | DBパスワード |
