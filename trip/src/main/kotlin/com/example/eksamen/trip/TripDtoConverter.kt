package com.example.eksamen.trip

import com.example.eksamen.trip.db.TripEntity
import com.example.eksamen.trip.dto.TripDto

object TripDtoConverter {

    fun transform(trips: TripEntity) : TripDto =
            trips.run { TripDto(tripId, place, durationDays, pricePerPerson) }

    fun transform(trip: Iterable<TripEntity>) : List<TripDto> = trip.map { transform(it) }
}