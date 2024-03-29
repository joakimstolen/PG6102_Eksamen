package com.example.eksamen.auth
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/auth/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/auth/WebSecurityConfig.kt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        private val dataSource: DataSource,
        private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {


    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    @Bean
    override fun authenticationManagerBean() : AuthenticationManager {
        return super.authenticationManagerBean()
    }

    /*
     * This is where we define all the access rules,
     * ie who is authorized to access what
     */

    override fun configure(http: HttpSecurity) {

        http
                .exceptionHandling().authenticationEntryPoint {req,response,e ->
                    response.setHeader("WWW-Authenticate","cookie")
                    response.sendError(401)
                }
                .and()
                .logout().logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .and()
                //
                /*
                    these rules are matched one at a time, in their order.
                    this is important to keep in mind if different URL templates
                    can match the same URLs
                 */
                .authorizeRequests()
                .antMatchers("/swagger*/**", "/v3/api-docs", "/actuator/**").permitAll()
                .antMatchers("/api/auth/user").authenticated()
                .antMatchers("/api/auth/signUp").permitAll()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/logout").permitAll()

                /*
                    whitelisting: deny everything by default,
                    unless it was explicitly allowed in the rules
                    above.
                 */
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                //
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    }


    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("""
                     SELECT username, password, enabled
                     FROM users
                     WHERE username=?
                     """)
                .authoritiesByUsernameQuery("""
                     SELECT x.username, y.authority
                     FROM users x, authorities y
                     WHERE x.username=? and y.username=x.username
                     """)
                .passwordEncoder(passwordEncoder)


    }


}