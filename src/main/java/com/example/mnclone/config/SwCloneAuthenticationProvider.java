package com.example.mnclone.config;

import com.example.mnclone.entity.User;
import com.example.mnclone.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class SwCloneAuthenticationProvider implements AuthenticationProvider {

    @Value("admin.username")
    private String adminUserName;
    @Value("admin.passwordHash")
    private String adminPasswordHash;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (StringUtils.isAnyBlank(username, password)) {
            throw new BadCredentialsException("username and password can not be empty");
        }

        String encodedPassword = passwordEncoder.encode(password);
        if (StringUtils.equals(username, adminUserName) && StringUtils.equals(encodedPassword, adminPasswordHash)) {
            return new UsernamePasswordAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority("COMPANY_ADMIN")));
        }

        Optional<User> customer = userRepository.findByEmail(username);
        if (customer.isPresent() && StringUtils.equals(encodedPassword, customer.get().getPasswordHash())) {
            return new MnCloneAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER")), customer.get().getId());
        }

        throw new UsernameNotFoundException("user not found");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
