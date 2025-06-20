package com.ohhoonim.component.response;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(controllers = { DefaultResponseApi.class })
public class ResponseIntegrationTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    @WithMockUser
    public void normal() {
        mockMvcTester.get().uri("/defaultResponse/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("{\"code\":\"SUCCESS\",\"data\":{\"greeting\":\"hi, there\"}}");
    }

    @Test
    @WithMockUser
    public void exception() {
        mockMvcTester.get().uri("/defaultResponse/exception")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("{\"code\":\"ERROR\",\"message\":\"처리 중 에러가 발생했습니다.\",\"data\":null}");
    }

    @Test
    @WithMockUser
    public void success() {
        mockMvcTester.get().uri("/defaultResponse/type/success")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("{\"code\":\"SUCCESS\",\"data\":{\"name\":\"matthew\",\"age\":23}}");

    }

    @Test
    @WithMockUser
    public void typeFail() {
        mockMvcTester.get().uri("/defaultResponse/type/fail")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("""
                        {\"code\":\"ERROR\",
                        \"message\":\"thif is fail message\",
                        \"data\":{\"name\":\"matthew\",\"age\":23}}
                        """);
    }

    @Test
    @WithMockUser
    public void string() {
        mockMvcTester.get().uri("/defaultResponse/string")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("{\"code\":\"SUCCESS\",\"data\":\"this is String\"}");
    }

    @Test
    @WithMockUser
    public void entityString() {
        mockMvcTester.get().uri("/defaultResponse/responseEntity/string")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("{\"code\":\"SUCCESS\",\"data\":\"entity string\"}");
    }

    @Test
    @WithMockUser
    public void entityObject() {
        mockMvcTester.get().uri("/defaultResponse/responseEntity/object")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatus2xxSuccessful()
                .bodyJson()
                .isStrictlyEqualTo("""
                            {\"code\":\"SUCCESS\",
                            \"data\":{\"name\":\"matthew\",\"age\":23}}
                        """);
    }
}
