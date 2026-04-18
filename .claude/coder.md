# コーダー向け規約

## パッケージ名

`com.example.kodoucho`（`com.example.shinraisavings` は旧名・使用禁止）

---

## Java コーディング規約

- クラス名：UpperCamelCase / メソッド名：lowerCamelCase
- インデント：スペース4つ
- **レイヤー責務：** Service層にビジネスロジックを集約。Controllerは薄く保つ
- バリデーション：`@Valid` + `BindingResult` を使用

---

## Security ルール

- パスワードはBCryptでハッシュ化（平文保存禁止）
- CSRFトークンは有効のまま維持（無効化禁止）
- URLレベルのアクセス制御は `SecurityConfig.java` に集約
- Thymeleafでのロール制御：`sec:authorize="hasRole('PARENT')"` を使用

---

## Chart.js 実装ルール

- ダッシュボード：棒グラフ（月次入金推移）
- 子供詳細：折れ線グラフ（貯蓄推移・6ヶ月/1年切り替え）
- グラフの色はCSS変数から取得（ハードコード禁止）
