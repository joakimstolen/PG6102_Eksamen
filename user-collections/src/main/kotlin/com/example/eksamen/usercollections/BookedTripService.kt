package com.example.eksamen.usercollections

import com.example.eksamen.trip.dto.TripCollectionDto
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.usercollections.db.BookedTrip
import com.example.eksamen.usercollections.model.Collection
import com.example.eksamen.usercollections.model.Trip
import com.example.eksamen.utils.response.WrappedResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.annotation.PostConstruct

@Repository
interface BookedTripRepository : CrudRepository<BookedTrip, Long>{

    fun findBookingByTripId(tripId: String): List<BookedTrip>
}

@Service
class BookedTripService(
        private val client: RestTemplate,
        private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory,
        private val bookedTripRepository: BookedTripRepository
) {

    companion object{
        private val log = LoggerFactory.getLogger(BookedTripService::class.java)
    }

    protected var collection: Collection? = null

    @Value("\${tripServiceAddress}")
    private lateinit var tripServiceAddress: String


    private lateinit var cb: CircuitBreaker



    @PostConstruct
    fun init(){
        cb = circuitBreakerFactory.create("circuitBreakerToTrips")
    }




    fun fetchData(tripId: String): TripDto?{

        //val version = "v1_000"
        val uri = UriComponentsBuilder
                .fromUriString("http://${tripServiceAddress.trim()}/api/trips/${tripId}")
                .build().toUri()


        return cb.run(
                {
                    client.exchange(
                            uri,
                            HttpMethod.GET,
                            null,
                            object : ParameterizedTypeReference<WrappedResponse<TripDto>>() {}).body?.data

                },
                { e ->
                    log.error("Failed to fetch data from Trips Service: ${e.message}")
                    null
                }
        )

    }


    fun markAsCanceled( tripId: String){

        val trip = bookedTripRepository.findBookingByTripId(tripId)
        trip.forEach { it.tripId="CANCELED" }
        bookedTripRepository.saveAll(trip)

    }





}