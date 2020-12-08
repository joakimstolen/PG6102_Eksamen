package com.example.eksamen.usercollections.dto

import io.swagger.annotations.ApiModelProperty

data class BookedTripDto(

        @get:ApiModelProperty("Id of the trip")
        var tripId: String? = null,

        @get:ApiModelProperty("Number of people booked for the trip that the user owns")
        var numberOfPeopleBooked: Int? = null,

        @get:ApiModelProperty("Number of trips booked that the user owns")
        var numberOfTrips: Int? = null
)