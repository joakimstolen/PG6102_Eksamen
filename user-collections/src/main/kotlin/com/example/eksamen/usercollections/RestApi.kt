package com.example.eksamen.usercollections

import com.example.eksamen.usercollections.db.UserService
import com.example.eksamen.usercollections.dto.*
import com.example.eksamen.utils.response.RestResponseFactory
import com.example.eksamen.utils.response.WrappedResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.lang.IllegalArgumentException



@Api(value = "/api/user-collections", description = "Operations on card collections owned by users")
@RequestMapping(
        path = ["/api/user-collections"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestAPI(
        private val userService: UserService
) {

    @ApiOperation("Retrieve trip collection information for a specific user")
    @GetMapping(path = ["/{userId}"])
    fun getUserInfo(
            @PathVariable("userId") userId: String
    ) : ResponseEntity<WrappedResponse<UserDto>> {

        val user = userService.findByIdEager(userId)
        if(user == null){
            return RestResponseFactory.notFound("User $userId not found")
        }

        return RestResponseFactory.payload(200, DtoConverter.transform(user))
    }

    @ApiOperation("Create a new user, with the given id")
    @PutMapping(path = ["/{userId}"])
    fun createUser(@PathVariable("userId") userId: String): ResponseEntity<WrappedResponse<Void>>{
        val ok = userService.registerNewUser(userId)
        return if(!ok)  RestResponseFactory.userFailure("User $userId already exist")
        else RestResponseFactory.noPayload(201)
    }

    @ApiOperation("Execute a command on a user's collection, like for example buying/milling cards")
    @PatchMapping(
            path = ["/{userId}"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)]
    )
    fun patchUser(
            @PathVariable("userId") userId: String,
            @RequestBody dto: PatchUserDto
    ): ResponseEntity<WrappedResponse<PatchResultDto>>{

        if(dto.command == null){
            return RestResponseFactory.userFailure("Missing command")
        }



        val tripId = dto.tripId
                ?: return RestResponseFactory.userFailure("Missing trip id")

        val nrOfPersons = dto.nrOfPersons
                ?: return RestResponseFactory.userFailure("Missing nr of persons")


        if(dto.command == Command.CANCEL_TRIP){
            try{
                userService.cancelTrip(userId, tripId)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to cancel trip $tripId")
            }
            return RestResponseFactory.payload(200, PatchResultDto())
        }


        if(dto.command == Command.ALTER_TRIP){
            try{
                userService.alterTrip(userId, tripId, nrOfPersons)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to alter trip $tripId")
            }
            return RestResponseFactory.payload(200, PatchResultDto())
        }

        return RestResponseFactory.userFailure("Unrecognized command: ${dto.command}")
    }


    @ApiOperation("Book trip")
    @PostMapping(
            path = ["/{userId}"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)]
    )
    fun bookTrip(
            @PathVariable("userId") userId: String,
            @RequestBody dto: BookedTripDto
    ): ResponseEntity<WrappedResponse<BookedTripDto>>{

        val tripId = dto.tripId
                ?: return RestResponseFactory.userFailure("Missing trip id")

        val nrOfPersons = dto.numberOfPeopleBooked
                ?: return RestResponseFactory.userFailure("Missing nr of persons")



        val numberOfTrips : Int = dto.numberOfTrips
                ?: return RestResponseFactory.userFailure("Missing nr of trips")


        userService.bookTrip(userId, tripId, nrOfPersons)



        return RestResponseFactory.payload(201, BookedTripDto())

//        val tripId = dto.tripId
//                ?: return RestResponseFactory.userFailure("Missing trip id")
//
//        val nrOfPersons = dto.numberOfPeopleBooked
//                ?: return RestResponseFactory.userFailure("Missing nr of persons")
//
//        val numberOfTrips = dto.numberOfTrips
//                ?: return RestResponseFactory.userFailure("Missing nr of trips")

//
//            try{
//                userService.bookTrip(userId, tripId, nrOfPersons)
//            } catch (e: IllegalArgumentException){
//                return RestResponseFactory.userFailure(e.message ?: "Failed to buy trip $tripId")
//            }
//            return ResponseEntity.status(201).build()

    }
}