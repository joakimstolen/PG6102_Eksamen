package com.example.eksamen.usercollections.db

import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.usercollections.BookedTripRepository
import com.example.eksamen.usercollections.BookedTripService
import com.example.eksamen.usercollections.FakeData
import com.example.eksamen.usercollections.RestAPITest
import com.example.eksamen.usercollections.model.Collection
import com.example.eksamen.utils.response.WrappedResponse
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import wiremock.com.fasterxml.jackson.databind.ObjectMapper




@ActiveProfiles("UserServiceTest,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = [(UserServiceTest.Companion.Initializer::class)])
internal class UserServiceTest{

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


    @BeforeEach
    fun initTest(){
        userRepository.deleteAll()
    }

    @Test
    fun testCreateUser(){
        val id = "foo"
        assertTrue(userService.registerNewUser(id))
        assertTrue(userRepository.existsById(id))
    }

    @Test
    fun testFailCreateUserTwice(){
        val id = "foo"
        assertTrue(userService.registerNewUser(id))
        assertFalse(userService.registerNewUser(id))
    }

//    @Test
//    fun testBuyTrip(){
//
//        val userId = "foo"
//        val tripId = "t002"
//        val nrOfPeople = 3
//
//        userService.registerNewUser(userId)
//        userService.bookTrip(userId, tripId, nrOfPeople)
//
//        val user = userService.findByIdEager(userId)!!
//        assertTrue(user.ownedBookedTrips.any { it.tripId == tripId})
//    }

//    @Test
//    fun testBuyTripFailNotEnoughMoney(){
//
//        val userId = "foo"
//        val tripId = "t04"
//        val nrOfPeople = 3
//
//        userService.registerNewUser(userId)
//
//        val e = assertThrows(IllegalArgumentException::class.java){
//            userService.bookTrip(userId, tripId, nrOfPeople)
//        }
//        assertTrue(e.message!!.contains("coin"), "Wrong error message: ${e.message}")
//    }

//    @Test
//    fun testCancelTrip(){
//
//        val userId = "foo"
//        val tripId = "t002"
//        val nrOfPeople = 4
//
//        userService.registerNewUser(userId)
//
//        val before = userRepository.findById(userId).get()
//        val coins = before.coins
//
//        userService.registerNewUser(userId)
//        userService.bookTrip(userId, tripId, nrOfPeople)
//
//        val between = userService.findByIdEager(userId)!!
//        val n = between.ownedBookedTrips.sumBy { it.numberOfTrips }
//        userService.cancelTrip(userId, between.ownedBookedTrips[0].tripId!!)
//
//
//        val after = userService.findByIdEager(userId)!!
//        //assertTrue(after.coins > coins)
//        assertEquals(n-1, after.ownedBookedTrips.sumBy { it.numberOfTrips })
//    }


//    @Test
//    fun testAlterTrip(){
//
//        val userId = "foo"
//        val tripId = "t002"
//        val nrOfPeople = 4
//
//        userService.registerNewUser(userId)
//
//        //checking that there is no persons before trip is booked
//        val before = userRepository.findById(userId).get()
//        assertEquals(before.nrOfPersons, 0)
//
//        userService.registerNewUser(userId)
//        userService.bookTrip(userId, tripId, nrOfPeople)
//
//        //checking if the correct amount of people is registered after the trip is booked
//        val between = userService.findByIdEager(userId)!!
//        assertEquals(between.nrOfPersons, nrOfPeople)
//
//        val newNrOfPeople = 10
//        userService.alterTrip(userId, tripId, newNrOfPeople)
//
//        //Checking if the altered amount of people is registered after the trip has been altered
//        val after = userService.findByIdEager(userId)!!
//        assertEquals(after.nrOfPersons, newNrOfPeople)
//    }


}