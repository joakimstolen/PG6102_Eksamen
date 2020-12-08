package com.example.eksamen.usercollections.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
class BookedTrip (
        @get:Id
        @get:GeneratedValue
        var id: Long? = null,

        @get:ManyToOne
        @get:NotNull
        var user: User? = null,

        @get:NotBlank
        var tripId: String? = null,

        @get:Min(0)
        var numberOfPeopleBooked : Int = 0,

        @get:Min(0)
        var numberOfTrips : Int = 0


)