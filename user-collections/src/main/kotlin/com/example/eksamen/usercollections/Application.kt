package com.example.eksamen.usercollections

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@SpringBootApplication(scanBasePackages = ["com.example.eksamen"])
class Application {

    @LoadBalanced
    @Bean
    fun loadBalancedClient() : RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun swaggerApi() : Docket {
        return Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build()
    }


    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("API for user-collection")
                .description("REST service to handle the trips booked by the user")
                .version("1.0")
                .build()
    }

    @Bean
    fun fanout(): FanoutExchange {
        return FanoutExchange("user-creation")
        //fetches the auth fanout
    }


    //although it is a broadcast, only one instance of `scores` and
    //only one instance of `user-collections` should receive and process such
    //messages (in case you have several replicated instances in your Docker-Compose file).
    //This means you need to use a named queue for each service kind
    //(i.e., a named queue for all instances of `scores` that is different
    //from the named queue for all instances of `user-collections`).
    //
    @Bean
    fun queue(): Queue {
        //making a queue
        return Queue("user-creation-user-collections")
    }

    @Bean
    fun binding(fanout: FanoutExchange,
                queue: Queue): Binding {
        //binding the queue to the auths fanout
        return BindingBuilder.bind(queue).to(fanout)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}