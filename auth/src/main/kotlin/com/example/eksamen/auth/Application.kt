package com.example.eksamen.auth

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

    /*
        In the Fanout exchange, messages are copied
        to all the bound queues.

        Note 1: on a RabbitMQ server, there can be
        many fanout exchanges.
        To distinguish them, names are used.

        Note 2: in a fanout exchange, the name of
        the queues is irrelevant, as such info is not
        used.

        Note 3: a sender just need to know of the fanout.
        A receiver needs to know about the fanout and just
        its own queue.
     */

    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("user-creation")
    }
}


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}