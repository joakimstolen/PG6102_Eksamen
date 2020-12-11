package com.example.eksamen.usercollections.model
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/model/Card.kt
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.trip.dto.TripType

data class Trip(
        val tripId : String,
        val tripType: TripType
){

    constructor(dto: TripDto): this(
            dto.tripId ?: throw IllegalArgumentException("Null tripId"),
            dto.tripType ?: throw IllegalArgumentException("Null tripType"))
}