package com.example.eksamen.trip

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import org.springframework.amqp.core.Queue
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@SpringBootApplication(scanBasePackages = ["com.example.eksamen"])
@EnableDiscoveryClient
class Application {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("API for trips")
                .description("REST service with info regarding all the trips")
                .version("1.0")
                .build()
    }

    @Bean
    fun creationFanout(): FanoutExchange {
        return FanoutExchange("trip-creation")
    }

    @Bean
    fun deletionFanout(): FanoutExchange {
        return FanoutExchange("trip-deletion")
    }

}


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}