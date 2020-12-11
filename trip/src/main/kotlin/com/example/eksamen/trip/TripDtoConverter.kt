package com.example.eksamen.trip
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/DtoConverter.kt
import com.example.eksamen.trip.db.TripEntity
import com.example.eksamen.trip.dto.TripDto

object TripDtoConverter {

    fun transform(trips: TripEntity) : TripDto =
            trips.run { TripDto(tripId, place, durationDays, pricePerPerson) }

    fun transform(trip: Iterable<TripEntity>) : List<TripDto> = trip.map { transform(it) }
}