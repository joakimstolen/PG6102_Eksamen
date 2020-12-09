package com.example.eksamen.trip

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http
                .exceptionHandling().authenticationEntryPoint {_, res , _ ->
                    res.setHeader("WWW-Authenticate","cookie")
                    res.sendError(401)
                }
                .and()
                .authorizeRequests()

                .antMatchers("/swagger*/**", "/v3/api-docs", "/actuator/**").permitAll()
                //everyone can see the list of trips
                //.antMatchers("/api/trips").permitAll()
                .antMatchers("/api/trips/collection_*").permitAll()

                //only admins can do modifying of trips
                .antMatchers(HttpMethod.GET, "/api/trips/**").permitAll()
                .antMatchers(HttpMethod.HEAD, "/api/trips/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/trips*/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/trips*/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/trips*/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/trips*/**").hasRole("ADMIN")
                //.antMatchers("/trips*/**").hasRole("ADMIN")
                //

                //
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                //never create a session, but use existing one if provided
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    @Bean
    fun userSecurity(): UserSecurity {
        return UserSecurity()
    }



    /**
     * Custom check. Not only we need a user authenticated, but we also
     * need to make sure that a user can only access his/her data, and not the
     * one of the other users
     */
    class UserSecurity {

        fun checkId(authentication: Authentication, id: String): Boolean {

            val username = (authentication.principal as UserDetails).username
            return username == id
        }
    }

}