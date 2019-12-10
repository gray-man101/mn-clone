package com.example.mnclone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SwCloneAuthenticationProvider swCloneAuthenticationProvider;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
//                .formLogin().successHandler((request, response, authentication) -> response.setStatus(200))
//                .and().authorizeRequests().anyRequest().authenticated().and()
                .httpBasic().disable()
                .cors().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin().successHandler((request, response, authentication) -> response.setStatus(200))
                .and()
                .csrf().disable();
    }

    //TODO no login for dev

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("aa")
//                .password("{noop}bb")
//                .roles("CUSTOMER")
//                .and()
//                .withUser("cc")
//                .password("{noop}dd")
//                .roles("COMPANY_ADMIN");
        auth.authenticationProvider(swCloneAuthenticationProvider);
    }


}
