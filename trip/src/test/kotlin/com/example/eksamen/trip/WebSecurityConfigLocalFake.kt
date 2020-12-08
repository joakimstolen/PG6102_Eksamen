package com.example.eksamen.trip

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint

@Configuration
@EnableWebSecurity
@Order(1)
class WebSecurityConfigLocalFake : WebSecurityConfig() {

    override fun configure(http: HttpSecurity) {
        //call method in parent-class to apply same settings
        super.configure(http)

        http.httpBasic()
                .and()
                .exceptionHandling().authenticationEntryPoint(BasicAuthenticationEntryPoint())
                .and()
                //but then override the session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.inMemoryAuthentication()
                .withUser("foo").password("{noop}123").roles("USER").and()
                .withUser("bar").password("{noop}123").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("ADMIN", "USER")
    }
}