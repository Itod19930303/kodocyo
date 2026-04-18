# デザイナー向けデザインシステム

> 詳細仕様は `DESIGN.md`（リポジトリルート）を参照すること。

---

## カラーパレット（主要）

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

---

## デザインルール

- **No-Line Rule：** 1px borderによるセクション区切り禁止
- **角丸：** 最小 `0.5rem`。シャープな角禁止
- **純黒禁止：** `#000000` 禁止 → `on-surface (#173355)` を使用
- **ボタン：** 必ずpill形状（`rounded-full`）
- **入力フィールド：** borderなし・`surface-container-low` 背景・`rounded-xl`
- **カード：** `surface-container-lowest` + ambient shadow
