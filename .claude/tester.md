# テスター向け TDD 規約

## TDD サイクル

Red（失敗テストを先に書く）→ Green（通す最小実装）→ Refactor（テストを壊さず整理）

実装ファイルを書く前に必ずテストファイルを作成すること。

---

## テスト実行コマンド

```bash
mvn test                          # 全テスト
mvn test -Dtest=ChildServiceTest  # 特定クラス
mvn verify                        # テスト + JaCoCo カバレッジ
```

---

## テスト種別と使い方

| 種別 | アノテーション | 用途 | DB不要 |
|---|---|---|---|
| Unit Test | `@ExtendWith(MockitoExtension.class)` | Service・ドメインロジック | ✅ |
| Controller Test | `@WebMvcTest(XController.class)` | HTTPレイヤー・バリデーション | ✅ |
| Security Test | `@WebMvcTest` + `@WithMockUser` | URLアクセス制御 | ✅ |
| Integration Test | `@SpringBootTest` + `@ActiveProfiles("test")` | 結合確認（H2使用） | ✗ |

> **原則：** Service/Controller テストは DB に触れない。

---

## テスト命名規則

```
メソッド名_条件_期待結果
```

例: `getBalance_noTransactions_returnsZero` / `childrenNew_asPartner_returns403`

---

## カバレッジ目標

- **Service層：80%以上**（`mvn verify` 失敗で検知）
- Controller層：主要ハッピーパス + バリデーションエラー + セキュリティ

---

## @WebMvcTest で必要な MockBean

全 `@WebMvcTest` クラスに以下が必要（`SecurityConfig` の依存のため）:
- `CustomUserDetailsService`
- `OAuth2UserService`

Controller が `LoginUser` を使う場合はさらに `LoginUser` も追加。

実装テンプレートは `.claude/references/tester-recipes.md` を参照。

---

## テスト禁止事項

- 実DBへの直接接続禁止 → H2 or Mockito を使用
- テスト間の状態共有禁止 → `@BeforeEach` でリセット
- ビジネスロジックを Controller に書いてテスト禁止 → Service に移動してからテスト
- `@SpringBootTest` を Service/Controller テストに使用禁止（重い）
