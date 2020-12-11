package com.example.eksamen.usercollections
//https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
//To override bean and not enable LoadBalanced
@Configuration
class RestTestTemplate {
    @Bean
    fun client(): RestTemplate{
        return RestTemplate()
    }
}