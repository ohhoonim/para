package com.ohhoonim.component.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableAsync
public class AppSecurity {

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder pe) {
        var user = User.builder()
                .username("john")
                .password(pe.encode("12345"))
                .authorities("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // return NoOpPasswordEncoder.getInstance(); 
        // return new BCryptPasswordEncoder();

        // "{bcrypt}navo342sdajf" 형태로 저장된 비밀번호를 인코딩할 때 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // default BrcyptPasswordEncoder

    }

    // async를 사용하는 경우를 위한 설정 inheritable threadlocal mode
    // global mode를 사용하는 경우 thread safe하지 않다 
    // 직접 thread를 만들어 사용하는 경우는 해당 안됨 
    @Bean
    InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(
            SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
        );
    }

}
