package com.example.eksamen.trip.service
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/scores/db/UserStatsServiceTest.kt
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("FakeData,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class TripServiceTest{

    @Autowired
    private lateinit var tripService: TripService

    @Autowired
    private lateinit var tripRepository: TripRepository

    @Test
    fun testInit(){
        assertTrue(tripRepository.count() > 10)
    }

    @Test
    fun createTrip(){
        val n = tripRepository.count()
        tripService.registerNewTrip("Foo001")
        assertEquals(n+1, tripRepository.count())
    }

    @Test
    fun testPage(){
        val n = 5
        val page = tripService.getNextPage(n)
        assertEquals(n, page.size)

        for (i in 0 until n-1){
            assertTrue(page[i].pricePerPerson!! >= page[i+1].pricePerPerson!!)
        }
    }


}