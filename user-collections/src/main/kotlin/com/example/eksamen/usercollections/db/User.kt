package com.example.eksamen.usercollections.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/db/User.kt
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "user_data")
class User(

        @get:Id
        @get:NotBlank
        var userId: String? = null,

        @get:Min(0)
        var coins: Int = 0,

        @get:Min(0)
        var nrOfPersons: Int = 0,

        @get:OneToMany(mappedBy = "user", cascade = [(CascadeType.ALL)])
        var ownedBookedTrips: MutableList<BookedTrip> = mutableListOf()


)