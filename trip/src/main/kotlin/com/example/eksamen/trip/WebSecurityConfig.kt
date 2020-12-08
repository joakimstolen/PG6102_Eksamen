package com.example.eksamen.trip

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http
                .exceptionHandling().authenticationEntryPoint {req,response,e ->
                    response.setHeader("WWW-Authenticate","cookie")
                    response.sendError(401)
                }
                .and()
                .authorizeRequests()

                .antMatchers("/swagger*/**", "/v3/api-docs", "/actuator/**").permitAll()
                //everyone can see the list of trips
                .antMatchers("/api/trips").permitAll()
                .antMatchers("/api/trips/collection_*").permitAll()

                //only admins can do modifying of trips
                .antMatchers("/api/trips/{tripId}").hasRole("ADMIN")
                //

                //
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                //never create a session, but use existing one if provided
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }



}