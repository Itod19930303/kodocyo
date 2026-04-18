# ルール例集

通常作業では読まなくてよい。コミットメッセージやコメントの書き方に迷ったときに参照する。

---

## コミットの良い例

```
feat: 子供削除時に確認ダイアログを追加

誤タップによる意図しない削除が報告されたため、
削除ボタン押下後に確認モーダルを挟む仕様に変更した。
```

```
fix: calcBalance で出金がマイナスになるバグを修正

sumExpenseByChildId の戻り値が負数になるケースで
残高計算が二重にマイナスされていた。
符号を統一し income - expense に修正。
```

```
test: ChildService の単体テストを追加

TDD導入に伴い、calcBalance・getGradeLabel・create の
正常系・異常系を網羅するテストを先行実装した。
```

## コミットの悪い例（禁止）

```
fix: バグ修正          ← 何のバグかわからない
update: いろいろ修正    ← type が不正、内容不明
feat: Add feature      ← 英語混じり
```

---

## Javaコメントの例

### クラスコメント（Javadoc）

```java
/**
 * 子供の貯蓄情報を管理するサービス。
 *
 * <p>残高はDBに持たず、入出金トランザクションから都度計算する（ドメインルール参照）。
 * キャッシュは行わないため、呼び出し回数に注意すること。
 */
@Service
public class ChildService { ... }
```

### メソッドコメント（Javadoc）

```java
/**
 * 子供の現在残高を計算して返す。
 *
 * @param childId 対象の子供ID
 * @return 残高（入金合計 - 出金合計）。トランザクションがゼロ件の場合は 0 を返す
 */
public int calcBalance(Long childId) { ... }
```

### インラインコメント（//）

```java
// expenseタイプの場合、フォームにcategoryが入っていても無視する（DBスキーマ上NULLが正）
.category("income".equals(form.getType()) ? form.getCategory() : null)

// 年齢は学年区切りで判定。日本の学年は4月始まりだが、
// ここでは誕生日ベースの満年齢で簡易判定している
if (age <= 5) grade = "幼稚園・保育園";
```

### 書いてはいけないコメント（禁止）

```java
// iをインクリメント
i++;

// nullチェック
if (child == null) { ... }

// ユーザーを保存する
userRepository.save(user);
```

---

## Thymeleafテンプレートのコメント例

```html
<!-- ロール別表示制御: PARENTのみ操作ボタンを表示（閲覧専用ロールには非表示） -->
<div sec:authorize="hasRole('PARENT')">
    ...
</div>
```

---

## SQLコメント例（Flyway マイグレーション）

```sql
-- V3__create_transactions.sql
-- 入出金トランザクションテーブル。
-- 残高はこのテーブルから都度計算するため、balanceカラムは持たない（PRO-001参照）。
CREATE TABLE transactions (
    id          BIGSERIAL PRIMARY KEY,
    child_id    BIGINT NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    type        VARCHAR(10) NOT NULL CHECK (type IN ('income', 'expense')),
    -- カテゴリは入金時のみ使用。出金時はNULLとする
    category    VARCHAR(20),
    amount      INTEGER NOT NULL CHECK (amount > 0),
    ...
);
```
