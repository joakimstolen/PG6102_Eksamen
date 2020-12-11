//https://www.journaldev.com/21429/spring-component
//https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/CommandLineRunner.html

package com.example.eksamen.auth

import com.example.eksamen.auth.db.UserRepository
import com.example.eksamen.auth.db.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitUserLoader (
        private val userRepository: UserRepository,
        private val userService: UserService
) : CommandLineRunner{

    //creating admin user initialized when SpringBoot runs
    override fun run(vararg args: String?) {
        createAdmin()
    }

    private fun createAdmin(){
        if(!userRepository.existsById("admin")){
            userService.createUser("admin", "admin", setOf("USER", "ADMIN"))
        }
    }

}