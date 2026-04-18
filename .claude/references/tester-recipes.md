# テストレシピ集

通常作業では読まなくてよい。テストコードを書くときに参照する。

---

## Service Unit Test（Mockito）

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

---

## Controller WebMvcTest

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

---

## LoginUser のセットアップ例

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

## セキュリティアクセステスト

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
