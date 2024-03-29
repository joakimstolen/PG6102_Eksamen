package com.example.eksamen.usercollections
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/RestAPITest.kt
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.usercollections.db.UserRepository
import com.example.eksamen.usercollections.db.UserService
import com.example.eksamen.usercollections.dto.BookedTripDto
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


            val tripDto = TripDto(tripId = "t002")
            val wrapped = WrappedResponse(code = 200, data = tripDto).validated()
            val json = ObjectMapper().writeValueAsString(wrapped)

            wiremockServer.stubFor(
                    WireMock.get(WireMock.urlMatching("/api/trips/t002"))
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
        val tripId = "t002"
        val nrOfPeople = 2

        given().auth().basic(userId, "123").put("/$userId").then().statusCode(201)

        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(BookedTripDto(tripId, nrOfPeople, 1))
                .post("/$userId")
                .then()
                .statusCode(201)

        val user = userService.findByIdEager(userId)!!
        assertTrue(user.ownedBookedTrips.any { it.tripId == tripId })
        assertTrue(user.ownedBookedTrips.any { it.numberOfPeopleBooked == nrOfPeople })
    }


    @Test
    fun testCancelTrip() {

        val userId = "foo"
        val tripIdToBuy = "t002"
        val nrOfPeople = 2

        given().auth().basic(userId, "123").put("/$userId").then().statusCode(201)

        val before = userRepository.findById(userId).get()
        val coins = before.coins

        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(BookedTripDto(tripIdToBuy, nrOfPeople, 1))
                .post("/$userId")
                .then()
                .statusCode(201)

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


    @Test
    fun testAlterTripPersons() {

        val userId = "foo"
        val tripIdToBuy = "t002"
        val nrOfPeople = 5

        given().auth().basic(userId, "123").put("/$userId").then().statusCode(201)

        val before = userRepository.findById(userId).get()
        val beforePersons = before.nrOfPersons

        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(BookedTripDto(tripIdToBuy, nrOfPeople, 1))
                .post("/$userId")
                .then()
                .statusCode(201)

        val between = userService.findByIdEager(userId)!!
        val betweenPersons = between.nrOfPersons
        assertTrue(betweenPersons == nrOfPeople)

        val newNrOfPeople = 8


        //val tripId = between.ownedBookedTrips[0].tripId!!
        given().auth().basic(userId, "123")
                .contentType(ContentType.JSON)
                .body(PatchUserDto(Command.ALTER_TRIP, tripIdToBuy, newNrOfPeople))
                .patch("/$userId")
                .then()
                .statusCode(200)

        //Checking ig number of persons booked is equals the altered amount of people booked
        val after = userService.findByIdEager(userId)!!
        assertTrue(after.nrOfPersons == newNrOfPeople)

        //checking if the trip is the correct one
        val tripId = between.ownedBookedTrips[0].tripId!!
        assertEquals(tripId, tripIdToBuy)


    }

}