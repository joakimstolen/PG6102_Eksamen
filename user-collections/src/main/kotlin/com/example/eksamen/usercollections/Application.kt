package com.example.eksamen.usercollections
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/Application.kt
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.time.Duration

@SpringBootApplication(scanBasePackages = ["com.example.eksamen"])
@EnableDiscoveryClient
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
    fun deletionFanout(): FanoutExchange {
        return FanoutExchange("trip-deletion")
        //fetches the trip fanout
    }

    @Bean
    fun creationFanout(): FanoutExchange {
        return FanoutExchange("trip-creation")
        //fetches the trip fanout
    }

    @Bean
    fun authCreationFanout(): FanoutExchange {
        return FanoutExchange("auth-creation")
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
    fun deletionQueue(): Queue {
        //making a queue
        return Queue("trip-deletion-user-collections")
    }


    @Bean
    fun creationQueue(): Queue {
        //making a queue
        return Queue("trip-creation-user-collections")
    }


    @Bean
    fun authCreationQueue(): Queue {
        //making a queue
        return Queue("auth-creation-user-collections")
    }





    @Bean
    fun creationBinding(creationFanout: FanoutExchange,
                creationQueue: Queue): Binding {
        //binding the queue to the trips fanout
        return BindingBuilder.bind(creationQueue).to(creationFanout)
    }

    @Bean
    fun deletionBinding(deletionFanout: FanoutExchange,
                deletionQueue: Queue): Binding {
        //binding the queue to the trips fanout
        return BindingBuilder.bind(deletionQueue).to(deletionFanout)
    }

    @Bean
    fun authCreationBinding(authCreationFanout: FanoutExchange,
                            authCreationQueue: Queue): Binding {
        //binding the queue to the auth fanout
        return BindingBuilder.bind(authCreationQueue).to(authCreationFanout)
    }



}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}