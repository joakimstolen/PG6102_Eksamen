package com.example.eksamen.trip.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-02/cards-dto/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/cards/dto/CardDto.kt
import io.swagger.annotations.ApiModelProperty

class TripDto (

        @get:ApiModelProperty("The id of the trip")
        var tripId : String? = null,

        @get:ApiModelProperty("The place of the trip")
        var place : String? = null,

        @get:ApiModelProperty("Duration of the trip")
        var durationDays : Int? = 0,

        @get:ApiModelProperty("Price per person of the trip")
        var pricePerPerson : Int? = 0,

        @get:ApiModelProperty("The type of trip")
        var tripType : TripType? = null
)