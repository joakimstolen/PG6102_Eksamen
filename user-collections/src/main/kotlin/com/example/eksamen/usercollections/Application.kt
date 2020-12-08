package com.example.eksamen.usercollections

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

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}