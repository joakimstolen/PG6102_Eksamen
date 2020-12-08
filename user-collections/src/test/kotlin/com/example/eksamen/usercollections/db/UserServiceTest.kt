package com.example.eksamen.usercollections.db

import com.example.eksamen.usercollections.BookedTripService
import com.example.eksamen.usercollections.FakeData
import com.example.eksamen.usercollections.model.Collection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@Profile("UserServiceTest")
@Primary
@Service
class FakeCardService : BookedTripService(RestTemplate(), Resilience4JCircuitBreakerFactory()){

    override fun fetchData() {
        val dto = FakeData.getCollectionDto()
        super.collection = Collection(dto)
    }
}


@ActiveProfiles("UserServiceTest,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class UserServiceTest{

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

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

    @Test
    fun testBuyTrip(){

        val userId = "foo"
        val tripId = "t00"
        val nrOfPeople = 3

        userService.registerNewUser(userId)
        userService.bookTrip(userId, tripId, nrOfPeople)

        val user = userService.findByIdEager(userId)!!
        assertTrue(user.ownedBookedTrips.any { it.tripId == tripId})
    }

    @Test
    fun testBuyTripFailNotEnoughMoney(){

        val userId = "foo"
        val tripId = "t04"
        val nrOfPeople = 3

        userService.registerNewUser(userId)

        val e = assertThrows(IllegalArgumentException::class.java){
            userService.bookTrip(userId, tripId, nrOfPeople)
        }
        assertTrue(e.message!!.contains("coin"), "Wrong error message: ${e.message}")
    }

    @Test
    fun testCancelTrip(){

        val userId = "foo"
        val tripId = "t01"
        val nrOfPeople = 4

        userService.registerNewUser(userId)

        val before = userRepository.findById(userId).get()
        val coins = before.coins

        userService.registerNewUser(userId)
        userService.bookTrip(userId, tripId, nrOfPeople)

        val between = userService.findByIdEager(userId)!!
        val n = between.ownedBookedTrips.sumBy { it.numberOfTrips }
        userService.cancelTrip(userId, between.ownedBookedTrips[0].tripId!!)


        val after = userService.findByIdEager(userId)!!
        //assertTrue(after.coins > coins)
        assertEquals(n-1, after.ownedBookedTrips.sumBy { it.numberOfTrips })
    }


}