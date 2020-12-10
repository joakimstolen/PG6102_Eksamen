package com.example.eksamen.usercollections

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestTestTemplate {
    @Bean
    fun client(): RestTemplate{
        return RestTemplate()
    }
}