package com.example.eksamen.usercollections.model

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