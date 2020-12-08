package com.example.eksamen.usercollections.dto

import io.swagger.annotations.ApiModelProperty

enum class Command {
    CANCEL_TRIP,
    BOOK_TRIP,
    ALTER_TRIP
}


data class PatchUserDto(

        @get:ApiModelProperty("Command to excecute on a users collection")
        var command: Command? = null,

        @get:ApiModelProperty("Optional trip id, if the command requires one")
        var tripId: String? = null,

        @get:ApiModelProperty("Optional Nr of persons, if the command requires one")
        var nrOfPersons: Int? = null

)