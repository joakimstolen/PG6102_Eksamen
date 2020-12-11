package com.example.eksamen.trip.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/db/UserStats.kt
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "trips")
class TripEntity(
        @get:Id
        @get:NotBlank
        var tripId: String? = null,

        @get:NotBlank
        var place: String? = null,

        @get:NotNull
        var durationDays: Int? = null,

        @get:NotNull
        var pricePerPerson: Int? = null
)