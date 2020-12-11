package com.example.eksamen.usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/UserDto.kt
import io.swagger.annotations.ApiModelProperty

data class UserDto(

        @get:ApiModelProperty("The id of the user")
        var userId: String? = null,

        @get:ApiModelProperty("The amount of coins owned by the user")
        var coins: Int? = null,

        @get:ApiModelProperty("The amount of persons booked")
        var nrOfPersons: Int? = null,

        @get:ApiModelProperty("List of trips owned by the user")
        var ownedBookedTrips: MutableList<BookedTripDto> = mutableListOf()
)