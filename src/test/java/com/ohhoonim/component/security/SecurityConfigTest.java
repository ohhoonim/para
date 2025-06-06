package com.ohhoonim.component.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(SecuritySampleApi.class)
public class SecurityConfigTest {
    // SecuritySampleApi.java

    @Autowired
    MockMvcTester mockMvc;

    @Test
    @WithMockUser
    public void securityGetMethodTest() {
        mockMvc.get().uri("/security/user")
                .param("user", "matthew")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat().apply(print())
                .hasStatusOk()
                .bodyJson().extractingPath("$.data")
                .isEqualTo("hi,matthew");

    }

    @Test
    @WithMockUser(username = "matthew")
    public void securityPostMethodTest() {
        // with(csrf())를 븉인이유는 
        // application.properties에 logging.level.org.springframework.security=trace
        // 설정하고 로그 살펴보면 알 수 있음. 아래 url 참고
        // https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/csrf.html
        mockMvc.post().with(csrf()).with(httpBasic("matthew", "1234"))
                .uri("/security/user")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat().apply(print())
                .hasStatusOk()
                .bodyJson().extractingPath("$.data")
                .isEqualTo("added user");

    }

}
