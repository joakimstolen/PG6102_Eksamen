package com.example.eksamen.trip

import com.example.eksamen.trip.dto.TripCollectionDto
import com.example.eksamen.trip.dto.TripDto
import com.example.eksamen.trip.dto.TripType


object TripCollection{

    fun get() : TripCollectionDto {

        val dto = TripCollectionDto()

        dto.prices.run {
            put(TripType.BRONZE, 100)
            put(TripType.SILVER, 500)
            put(TripType.GOLD,   1_000)
            put(TripType.PINK_DIAMOND, 100_000)
        }

        dto.prices.forEach { dto.sellValues[it.key] = it.value / 4 }

        dto.tripTypeProbabilities.run {
            put(TripType.SILVER, 0.10)
            put(TripType.GOLD, 0.01)
            put(TripType.PINK_DIAMOND, 0.001)
            put(TripType.BRONZE, 1 - get(TripType.SILVER)!! - get(TripType.GOLD)!! - get(TripType.PINK_DIAMOND)!!)
        }

        addCards(dto)

        return dto
    }

    private fun addCards(dto: TripCollectionDto){

        dto.trips.run {
            add(TripDto("t000", "TEST1", 2,200, TripType.BRONZE))
            add(TripDto("t001", "TEST2", 2,200, TripType.BRONZE))
            add(TripDto("t002", "TEST3", 2,200, TripType.BRONZE))
            add(TripDto("t003", "TEST4", 2,200, TripType.BRONZE))
            add(TripDto("t004", "TEST5", 2,200, TripType.BRONZE))
            add(TripDto("t005", "TEST6", 2,200, TripType.BRONZE))
            add(TripDto("t006", "TEST7", 2,200, TripType.BRONZE))
        }

        assert(dto.trips.size == dto.trips.map { it.tripId }.toSet().size)
        assert(dto.trips.size == dto.trips.map { it.place }.toSet().size)
    }

}