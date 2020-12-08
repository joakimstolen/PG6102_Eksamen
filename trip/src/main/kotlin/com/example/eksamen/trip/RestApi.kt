package com.example.eksamen.trip

import com.example.eksamen.trip.db.TripEntity
import com.example.eksamen.trip.dto.TripCollectionDto
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.trip.service.TripRepository
import com.example.eksamen.trip.service.TripService
import com.example.eksamen.utils.response.PageDto
import com.example.eksamen.utils.response.RestResponseFactory
import com.example.eksamen.utils.response.WrappedResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

@Api(value = "/api/trips", description = "Operation on the existing trips")
@RequestMapping(path = ["/api/trips"])
@RestController
class RestApi(
        private val tripRepository: TripRepository,
        private val tripService: TripService
){

    companion object{
        const val LATEST = "v1_000"
    }


    @ApiOperation("Return info on all trips")
    @GetMapping(path = ["/collection_$LATEST"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLatest() : ResponseEntity<WrappedResponse<Iterable<TripEntity>>> {

        val collection = tripRepository.findAll()

        return ResponseEntity
                .status(200)
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(WrappedResponse(200, collection).validated())
    }


    @ApiOperation("Retrieve the current trip")
    @GetMapping(path = ["/{tripId}"])
    fun getTripInfo(@PathVariable("tripId") tripId: String): ResponseEntity<WrappedResponse<TripDto>> {

        val trip = tripRepository.findById(tripId).orElse(null)
        if (trip == null){
            return RestResponseFactory.notFound("trip $tripId not found")
        }

        return RestResponseFactory.payload(200, TripDtoConverter.transform(trip))
    }

    @ApiOperation("Update default info for new trip")
    @PutMapping(path = ["/{tripId}"])
    fun updateMovie(@PathVariable("tripId") tripId: String) : ResponseEntity<WrappedResponse<Void>> {
        val ok = tripService.registerNewTrip(tripId)
        return if(!ok) RestResponseFactory.userFailure("Trip $tripId already exists")
        else RestResponseFactory.noPayload(201)
    }


    @ApiOperation("Create new trip")
    @PostMapping(path = ["/{tripId}"])
    fun createMovie(@PathVariable("tripId") tripId: String) : ResponseEntity<WrappedResponse<Void>> {
        val ok = tripService.registerNewTrip(tripId)
        return if(!ok) RestResponseFactory.userFailure("Trip $tripId already exists")
        else RestResponseFactory.noPayload(201)
    }


    @ApiOperation("Return an iterable page of trips, starting highest price")
    @GetMapping
    fun getAllMovies(
            @ApiParam("title of trip in prev page")
            @RequestParam("keysetId", required = false)
            keysetId: String?,
            @ApiParam("Price of trips in prev page")
            @RequestParam("keysetPrice", required = false)
            keysetPrice: Int?) : ResponseEntity<WrappedResponse<PageDto<TripDto>>> {

        val page = PageDto<TripDto>()

        val n = 5
        val trip = TripDtoConverter.transform(tripService.getNextPage(n, keysetId, keysetPrice))
        page.list = trip

        if (trip.size == n){
            val last = trip.last()
            page.next = "/api/trips?keysetId=${last.tripId}&keysetPrice=${last.pricePerPerson}"
        }

        return ResponseEntity.status(200).cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
                .body(WrappedResponse(200, page).validated())

    }


    @ApiOperation("Delete trip, with given id")
    @DeleteMapping(path = ["/{tripId}"])
    fun deleteMovie(@PathVariable("tripId") tripId: String): ResponseEntity<WrappedResponse<Void>> {
        val id : String

        try {
            id = tripId
        } catch (e: Exception){
            return ResponseEntity.status(400).build()
        }

        if (!tripRepository.existsById(id)){
            return ResponseEntity.status(404).build()
        }

        tripRepository.deleteById(id)
        return ResponseEntity.status(204).build()

    }


}