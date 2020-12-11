package com.example.eksamen.usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/PatchResultDto.kt
import io.swagger.annotations.ApiModelProperty

class PatchResultDto (

        @get:ApiModelProperty("If a trip was altered, specify which trips are left")
        var tripIdsInCollectionAfterAlter: MutableList<String> = mutableListOf()
)