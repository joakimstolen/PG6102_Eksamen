package com.example.eksamen.usercollections

import com.example.eksamen.usercollections.db.UserRepository
import com.example.eksamen.usercollections.db.UserService
import com.example.eksamen.usercollections.dto.Command
import com.example.eksamen.usercollections.dto.PatchUserDto
import com.example.eksamen.utils.response.WrappedResponse
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
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
import wiremock.com.fasterxml.jackson.databind.ObjectMapper
import javax.annotation.PostConstruct

@ActiveProfiles("RestAPITest,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestAPITest.Companion.Initializer::class)])
internal class RestAPITest{

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {

        private lateinit var wiremockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun initClass() {
            wiremockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().notifier(ConsoleNotifier(true)))
            wiremockServer.start()


            val dto = WrappedResponse(code = 200, data = FakeData.getCollectionDto()).validated()
            val json = ObjectMapper().writeValueAsString(dto)

            wiremockServer.stubFor(
                    WireMock.get(WireMock.urlMatching("/api/trips/collection_.*"))
                            .willReturn(WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json; charset=utf-8")
                                    .withBody(json)))
        }


        @AfterAll
        @JvmStatic
        fun tearDown() {
            wiremockServer.stop()
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of("tripServiceAddress: localhost:${wiremockServer.port()}")
                        .applyTo(configurableApplicationContext.environment)
            }
        }
    }

    @PostConstruct
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/user-collections"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @BeforeEach
    fun initTest() {
        userRepository.deleteAll()
    }


    @Test
    fun testGetUser(){

        val id = "foo"
        userService.registerNewUser(id)

        RestAssured.given().auth().basic(id, "123")
                .get("/$id")
                .then()
                .statusCode(200)
    }

    @Test
    fun testCreateUser() {
        val id = "foo"

        RestAssured.given().auth().basic(id, "123")
                .put("/$id")
                .then()
                .statusCode(201)

        assertTrue(userRepository.existsById(id))
    }

    @Test
    fun testBookTrip() {

        val userId = "foo"
        val tripId = "t01"
        val nrOfPeople = 2

        given().auth().basic(userId, "123").put("/$userId").then().statusCode(201)

        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(PatchUserDto(Command.BOOK_TRIP, tripId, nrOfPeople))
                .patch("/$userId")
                .then()
                .statusCode(200)

        val user = userService.findByIdEager(userId)!!
        assertTrue(user.ownedBookedTrips.any { it.tripId == tripId })
        assertTrue(user.ownedBookedTrips.any { it.numberOfPeopleBooked == nrOfPeople })
    }


    @Test
    fun testCancelTrip() {

        val userId = "foo"
        val tripIdToBuy = "t01"
        val nrOfPeople = 2

        given().auth().basic(userId, "123").put("/$userId").then().statusCode(201)

        val before = userRepository.findById(userId).get()
        val coins = before.coins

        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(PatchUserDto(Command.BOOK_TRIP, tripIdToBuy, nrOfPeople))
                .patch("/$userId")
                .then()
                .statusCode(200)

        val between = userService.findByIdEager(userId)!!
        val n = between.ownedBookedTrips.sumBy { it.numberOfTrips }


        val tripId = between.ownedBookedTrips[0].tripId!!
        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(PatchUserDto(Command.CANCEL_TRIP, tripId, nrOfPeople))
                .patch("/$userId")
                .then()
                .statusCode(200)

        val after = userService.findByIdEager(userId)!!
        //assertTrue(after.coins > coins)
        assertEquals(n - 1, after.ownedBookedTrips.sumBy { it.numberOfTrips })
    }

}