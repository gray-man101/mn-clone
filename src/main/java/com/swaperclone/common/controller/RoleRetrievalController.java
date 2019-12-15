package com.swaperclone.common.controller;

import com.swaperclone.config.MnCloneAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('ROLE_COMPANY_ADMIN', 'ROLE_CUSTOMER')")
@RequestMapping("/api/role")
public class RoleRetrievalController {
    @GetMapping
    public Map<String, String> retrieveRole(MnCloneAuthenticationToken auth) {
        String role = auth.getAuthorities().toArray()[0].toString();
        return new HashMap<String, String>() {{
            put("role", role);
            put("homePath", "ROLE_COMPANY_ADMIN".equals(role) ? "/admin" : "/");
        }};
    }
}
