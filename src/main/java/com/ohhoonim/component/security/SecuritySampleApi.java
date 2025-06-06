package com.ohhoonim.component.security;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuritySampleApi {

    @GetMapping("/security/user")
    public String hiUser(@Param("user") String user) {
        return "hi," + user;
    }

    @PostMapping("/security/user")
    public String registUser() {
        return "added user";
    }
}
