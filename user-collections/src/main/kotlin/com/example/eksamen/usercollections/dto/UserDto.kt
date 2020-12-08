package com.example.eksamen.usercollections.dto

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