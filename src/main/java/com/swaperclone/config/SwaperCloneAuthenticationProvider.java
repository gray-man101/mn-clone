package com.swaperclone.config;

import com.swaperclone.common.entity.User;
import com.swaperclone.common.repository.UserRepository;
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
public class SwaperCloneAuthenticationProvider implements AuthenticationProvider {

    @Value("${admin.username}")
    private String adminUserName;
    @Value("${admin.passwordHash}")
    private String adminEncodedPassword;
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

        if (StringUtils.equals(username, adminUserName) && passwordEncoder.matches(password, adminEncodedPassword)) {
            return new MnCloneAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY_ADMIN")), -1L);
        }

        Optional<User> customer = userRepository.findRegisteredByEmail(username);
        if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getEncodedPassword())) {
            return new MnCloneAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")), customer.get().getId());
        }

        throw new UsernameNotFoundException("user not found");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
