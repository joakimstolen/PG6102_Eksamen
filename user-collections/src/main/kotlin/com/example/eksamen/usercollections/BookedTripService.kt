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
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.annotation.PostConstruct

@Service
class BookedTripService(
        private val client: RestTemplate,
        private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {

    companion object{
        private val log = LoggerFactory.getLogger(BookedTripService::class.java)
    }

    protected var collection: Collection? = null

    @Value("\${tripServiceAddress}")
    private lateinit var tripServiceAddress: String


    val tripCollection : List<Trip>
        get() = collection?.trips ?: listOf()

    private val lock = Any()

    private lateinit var cb: CircuitBreaker



    @PostConstruct
    fun init(){
        cb = circuitBreakerFactory.create("circuitBreakerToTrips")
    }

//    fun verifyCollection(){
//
//        if(collection == null){
//            fetchData()
//            log.info(collection)
//
//            if(collection == null){
//                throw IllegalStateException("No collection info")
//            }
//        }
//    }



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


//        if (response.statusCodeValue != 200) {
//            log.error("Error in fetching data from Trips Service. Status ${response.statusCodeValue}." +
//                    "Message: " + response.body.message)
//        }
//
//        try {
//            collection = Collection(response.body.data!!)
//            log.info(Collection(response.body.data!!).toString())
//        } catch (e: Exception) {
//            log.error("Failed to parse trip collection info: ${e.message}")
//        }
    }

    fun isInitialized() = tripCollection.isNotEmpty()



    fun sellValue(tripId: String) : Int {
        //verifyCollection()
        val trip : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid tripId $tripId")

        return collection!!.sellValues[trip.tripType]!!
    }

    fun price(tripId: String) : Int {
        //verifyCollection()
        val trip : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid tripId $tripId")

        return collection!!.prices[trip.tripType]!!
    }











}