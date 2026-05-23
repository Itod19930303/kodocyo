package com.example.kodoucho.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.email-verification")
@Getter @Setter
public class EmailVerificationProperties {

    /** 空文字の場合はユーザーの実際のメールアドレスに送付 */
    private String overrideTo = "test@example.com";

    /** 再認証が必要になるまでの日数 */
    private int requiredDays = 7;

    /** 認証メールのリンクベースURL */
    private String baseUrl = "http://localhost:8080";

    /** トークン有効期限（時間） */
    private int tokenExpiryHours = 24;
}
