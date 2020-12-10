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
            add(TripDto(tripId = "t000", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t001", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t002", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t003", tripType = TripType.BRONZE))
            add(TripDto(tripId = "t004", tripType = TripType.SILVER))
            add(TripDto(tripId = "t005", tripType = TripType.SILVER))
            add(TripDto(tripId = "t006", tripType = TripType.SILVER))
            add(TripDto(tripId = "t007", tripType = TripType.GOLD))
            add(TripDto(tripId = "t008", tripType = TripType.GOLD))
            add(TripDto(tripId = "t009", tripType = TripType.GOLD))
            add(TripDto(tripId = "t100", tripType = TripType.PINK_DIAMOND))
        }

        return dto
    }
}