package com.example.eksamen.auth

import com.example.eksamen.auth.db.UserService
import io.swagger.annotations.ApiOperation
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/auth")
class RestApi(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val rabbit: RabbitTemplate,
        private val fanout: FanoutExchange
) {

    @ApiOperation("get info on signed in user")
    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(map)
    }

    @ApiOperation("Create a new user")
    @PostMapping(
            path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signUp(@RequestBody dto: AuthDto)
            : ResponseEntity<Void> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val registered = service.createUser(userId, password, setOf("USER"))

        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        rabbit.convertAndSend(fanout.name, "", userId)

        return ResponseEntity.status(201).build()
    }

    @ApiOperation("Login a user")
    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthDto)
            : ResponseEntity<Void> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val userDetails = try{
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException){
            return ResponseEntity.status(400).build()
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).build()
    }

}