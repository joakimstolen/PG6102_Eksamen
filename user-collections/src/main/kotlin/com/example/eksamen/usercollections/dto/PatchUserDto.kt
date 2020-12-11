package com.example.eksamen.usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/PatchUserDto.kt
import io.swagger.annotations.ApiModelProperty

enum class Command {
    CANCEL_TRIP,
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