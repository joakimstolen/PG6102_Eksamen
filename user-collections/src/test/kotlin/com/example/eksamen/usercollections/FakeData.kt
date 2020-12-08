package com.example.eksamen.usercollections

import com.example.eksamen.trip.dto.TripCollectionDto
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.trip.dto.TripType

object FakeData {

    fun getCollectionDto() : TripCollectionDto{

        val dto = TripCollectionDto()

        dto.prices[TripType.BRONZE] = 100
        dto.prices[TripType.SILVER] = 500
        dto.prices[TripType.GOLD] = 1_000
        dto.prices[TripType.PINK_DIAMOND] = 100_000

        dto.prices.forEach { dto.sellValues[it.key] = it.value / 4 }
        dto.prices.keys.forEach { dto.tripTypeProbabilities[it] = 0.25 }

        dto.trips.run {
            add(TripDto(tripId = "t00", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t01", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t02", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t03", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t04", tripType = TripType.SILVER))
            add(TripDto(tripId = "t05", tripType = TripType.SILVER))
            add(TripDto(tripId = "t06", tripType = TripType.SILVER))
            add(TripDto(tripId = "t07", tripType = TripType.GOLD))
            add(TripDto(tripId = "t08", tripType = TripType.GOLD))
            add(TripDto(tripId = "t09", tripType = TripType.GOLD))
            add(TripDto(tripId = "t10", tripType = TripType.PINK_DIAMOND))
        }

        return dto
    }
}