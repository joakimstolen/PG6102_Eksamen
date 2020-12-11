package com.example.eksamen.usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/CardCopyDto.kt
import io.swagger.annotations.ApiModelProperty

data class BookedTripDto(

        @get:ApiModelProperty("Id of the trip")
        var tripId: String? = null,

        @get:ApiModelProperty("Number of people booked for the trip that the user owns")
        var numberOfPeopleBooked: Int? = null,

        @get:ApiModelProperty("Number of trips booked that the user owns")
        var numberOfTrips: Int? = null
)