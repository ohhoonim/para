package com.ohhoonim.component.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityFilterConfig {

    private final AppAuthenticationProvider appAuthenticationProvider;

    SecurityFilterConfig(AppAuthenticationProvider appAuthenticationProvider) {
        this.appAuthenticationProvider = appAuthenticationProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // authorization server로  사용할 때
        // http.with(authorizationServer(), as -> as.oidc(Customizer.withDefaults()));
        
        http.csrf(csrf -> csrf.disable());
        http.formLogin(Customizer.withDefaults());
        
        // httpbasic 방식 : 이건 사용안함 
        // http.httpBasic(Customizer.withDefaults());

        // passkey를 사용하는 방법. 지문 등
        // http.webAuthn(Customizer.withDefaults())

        // 비번변경할 때 이메일 보내고 일회성 접근 토큰을 발급하는 방법
        // http.oneTimeTokenLogin(c -> 
        //         c.tokenGenerationSuccessHandler());

        http.authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated());

        http.authenticationProvider(appAuthenticationProvider);

        return http.build();
    }

}
