package com.example.eksamen.auth
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/auth/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/auth/SecurityTest.kt
import com.example.eksamen.auth.db.UserRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@ActiveProfiles("test")
@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(SecurityTest.Companion.Initializer::class)])
class SecurityTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @LocalServerPort
    private var port = 0


    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @Container
        @JvmField
        val redis = KGenericContainer("redis:latest").withExposedPorts(6379)

        @Container
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                TestPropertyValues
                        .of("spring.redis.host=${redis.containerIpAddress}",
                                "spring.redis.port=${redis.getMappedPort(6379)}",
                                "spring.rabbitmq.host=" + rabbitMQ.containerIpAddress,
                                "spring.rabbitmq.port=" + rabbitMQ.getMappedPort(5672))
                        .applyTo(configurableApplicationContext.environment);
            }
        }
    }


    @BeforeEach
    fun initialize() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.basePath = "/api/auth"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        userRepository.deleteAll()
    }


    @Test
    fun testUnauthorizedAccess() {

        RestAssured.given().get("/user")
                .then()
                .statusCode(401)
    }


    /**
     *   Utility function used to create a new user in the database
     */
    private fun registerUser(id: String, password: String): String {


        val sessionCookie = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthDto(id, password))
                .post("/signUp")
                .then()
                .statusCode(201)
                .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                .extract().cookie("SESSION")

        /*
            From now on, the user is authenticated.
            I do not need to use userid/password in the following requests.
            But each further request will need to have the SESSION cookie.
         */

        return sessionCookie
    }

    private fun checkAuthenticatedCookie(cookie: String, expectedCode: Int){
        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedCode)
    }

    @Test
    fun testLogin() {

        val name = "foo"
        val pwd = "bar"

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = registerUser(name, pwd)

        RestAssured.given().get("/user")
                .then()
                .statusCode(401)

        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("data.name", CoreMatchers.equalTo(name))
                .body("data.roles", Matchers.contains("ROLE_USER"))



        /*
            Trying to login again will reset
            the SESSION token.
         */
        val login = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        Assertions.assertNotEquals(login, cookie)
        checkAuthenticatedCookie(login, 200)
    }



    @Test
    fun testWrongLogin() {

        val name = "foo"
        val pwd = "bar"

        val noAuth = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        //session is not created if not required, eg when 400 user error
        Assertions.assertNull(noAuth)

        registerUser(name, pwd)

        val auth = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }
}