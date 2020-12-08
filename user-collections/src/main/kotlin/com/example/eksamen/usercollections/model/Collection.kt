package com.example.eksamen.usercollections.model

import com.example.eksamen.trip.dto.TripCollectionDto
import com.example.eksamen.trip.dto.TripType
import kotlin.math.abs

data class Collection(

        val trips : List<Trip>,

        val prices: Map<TripType, Int>,

        val sellValues: Map<TripType, Int>,

        val tripTypeProbabilities: Map<TripType, Double>
){

    constructor(dto: TripCollectionDto) : this(
            dto.trips.map { Trip(it) },
            dto.prices.toMap(),
            dto.sellValues.toMap(),
            dto.tripTypeProbabilities.toMap()
    )

    val tripsByTripType : Map<TripType, List<Trip>> = trips.groupBy { it.tripType }

    init{
        if(trips.isEmpty()){
            throw IllegalArgumentException("No trips")
        }
        TripType.values().forEach {
            requireNotNull(prices[it])
            requireNotNull(sellValues[it])
            requireNotNull(tripTypeProbabilities[it])
        }

        val p = tripTypeProbabilities.values.sum()
        if(abs(1 - p) > 0.00001){
            throw IllegalArgumentException("Invalid probability sum: $p")
        }
    }
}