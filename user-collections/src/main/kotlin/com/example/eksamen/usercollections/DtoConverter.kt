package com.example.eksamen.usercollections

import com.example.eksamen.usercollections.db.BookedTrip
import com.example.eksamen.usercollections.db.User
import com.example.eksamen.usercollections.dto.BookedTripDto
import com.example.eksamen.usercollections.dto.UserDto
import com.example.eksamen.usercollections.model.Trip

object DtoConverter {


    fun transform(user: User) : UserDto {

        return UserDto().apply {
            userId = user.userId
            coins = user.coins
            ownedBookedTrips = user.ownedBookedTrips.map { transform(it) }.toMutableList()
        }
    }

    fun transform(bookedTrip: BookedTrip) : BookedTripDto {
        return BookedTripDto().apply {
            tripId = bookedTrip.tripId
            numberOfPeopleBooked = bookedTrip.numberOfPeopleBooked
        }
    }
}