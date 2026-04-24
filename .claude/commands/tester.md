# テスター向け TDD 規約

## TDD サイクル

```
Red → Green → Refactor
```

1. **Red：** 失敗するテストを先に書く
2. **Green：** テストが通る最小限の実装をする
3. **Refactor：** テストを壊さずにコードを整理する

実装ファイルを書く前に必ずテストファイルを作成すること。

---

## テスト実行コマンド

```bash
# 全テスト実行
mvn test

# 特定クラスのみ
mvn test -Dtest=ChildServiceTest

# テスト + JaCoCo カバレッジレポート生成
mvn verify

# レポート確認（ブラウザで開く）
open target/site/jacoco/index.html
```

---

## テスト種別と使い方

| 種別 | アノテーション | 用途 | DB不要 |
|---|---|---|---|
| Unit Test | `@ExtendWith(MockitoExtension.class)` | Service・ドメインロジック | ✅ |
| Controller Test | `@WebMvcTest(XController.class)` | HTTPレイヤー・バリデーション | ✅ |
| Security Test | `@WebMvcTest` + `@WithMockUser` | URLアクセス制御 | ✅ |
| Integration Test | `@SpringBootTest` + `@ActiveProfiles("test")` | 結合確認（H2使用） | ✗ |

> **原則：** Service/Controller テストは DB に触れない。H2 は `@SpringBootTest` 専用。

---

## テストディレクトリ構成

```
src/test/
├── java/com/example/kodoucho/
│   ├── service/          # Unit Tests（Mockito）
│   │   ├── ChildServiceTest.java       ← 実装済み
│   │   └── TransactionServiceTest.java ← 実装済み
│   ├── controller/       # WebMvcTest
│   │   └── ChildControllerTest.java    ← 実装済み
│   └── security/         # アクセス制御テスト
│       └── SecurityAccessTest.java     ← 実装済み
└── resources/
    └── application-test.yml  # H2 インメモリDB設定（@SpringBootTest用）
```

---

## テスト命名規則

```
メソッド名_条件_期待結果
```

例：
- `getBalance_noTransactions_returnsZero`
- `create_invalidAmount_throwsException`
- `childrenNew_asPartner_returns403`

---

## カバレッジ目標

- **Service層：80%以上**（JaCoCo で自動チェック。`mvn verify` 失敗で検知）
- Controller層：主要ハッピーパス + バリデーションエラー + セキュリティ

---

## @WebMvcTest で必要な MockBean

`SecurityConfig` が `CustomUserDetailsService` と `OAuth2UserService` に依存するため、
全 `@WebMvcTest` クラスに以下の `@MockBean` が必要：

```java
@MockBean CustomUserDetailsService customUserDetailsService;
@MockBean OAuth2UserService oAuth2UserService;
```

Controller が `LoginUser` を使う場合はさらに：

```java
@MockBean LoginUser loginUser;

@BeforeEach
void setUp() {
    User testUser = User.builder().id(1L).name("テスト").email("test@test.com")
            .role("PARENT").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    when(loginUser.get(any(Authentication.class))).thenReturn(testUser);
}
```

---

## テストパターン集

### Service Unit Test（Mockito）

```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {
    @Mock XxxRepository xxxRepository;
    @InjectMocks XxxService xxxService;

    @Test
    void methodName_condition_expectedResult() {
        // Arrange
        when(xxxRepository.findById(1L)).thenReturn(Optional.of(...));

        // Act
        var result = xxxService.someMethod(1L);

        // Assert
        assertThat(result).isEqualTo(...);
        verify(xxxRepository).findById(1L);
    }
}
```

### Controller WebMvcTest

```java
@WebMvcTest(XxxController.class)
class XxxControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean XxxService xxxService;
    @MockBean LoginUser loginUser;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean OAuth2UserService oAuth2UserService;

    @Test
    @WithMockUser(roles = "PARENT")
    void showForm_asParent_returns200() throws Exception {
        mockMvc.perform(get("/xxx/new"))
               .andExpect(status().isOk())
               .andExpect(view().name("xxx/form"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    void submitForm_validData_redirects() throws Exception {
        mockMvc.perform(post("/xxx")
                        .param("field", "value")
                        .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }
}
```

### セキュリティアクセステスト

```java
@Test
void endpoint_unauthenticated_redirectsToLogin() throws Exception {
    mockMvc.perform(get("/protected"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("**/login"));
}

@Test
@WithMockUser(roles = "PARTNER")
void endpoint_wrongRole_returns403() throws Exception {
    mockMvc.perform(get("/parent-only"))
           .andExpect(status().isForbidden());
}
```

---

## テスト禁止事項

- 実DBへの直接接続禁止 → H2 or Mockito を使用
- テスト間の状態共有禁止 → `@BeforeEach` でリセット
- ビジネスロジックを Controller に書いてテストすること禁止 → Service に移動してからテスト
- `@SpringBootTest` を Service/Controller テストに使用禁止（重い・遅い）
