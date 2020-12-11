package com.example.eksamen.auth
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/auth/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/auth/Application.kt
import com.example.eksamen.auth.db.UserService
import org.springframework.amqp.core.FanoutExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
@EnableDiscoveryClient
class Application{




    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }



    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("auth-creation")
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}