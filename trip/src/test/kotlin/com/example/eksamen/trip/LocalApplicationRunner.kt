package com.example.eksamen.trip
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/scores/LocalApplicationRunner.kt
import org.springframework.boot.SpringApplication

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, "--spring.profiles.active=test")
}