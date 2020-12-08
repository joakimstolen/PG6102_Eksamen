package com.example.eksamen.trip.dto

import io.swagger.annotations.ApiModelProperty

class TripDto (

        @get:ApiModelProperty("The id of the trip")
        var tripId : String? = null,

        @get:ApiModelProperty("The name of the trip")
        var name : String? = null,

        @get:ApiModelProperty("The type of trip")
        var tripType : TripType? = null,

        @get:ApiModelProperty("Duration of the trip")
        var duration : Int? = 0,

        @get:ApiModelProperty("Price per person of the trip")
        var pricePerPerson : Int? = 0
)