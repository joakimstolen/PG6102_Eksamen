package com.example.eksamen.trip.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-02/cards-dto/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/cards/dto/CollectionDto.kt
class TripCollectionDto (

        var trips : MutableList<TripDto> = mutableListOf(),

        var prices: MutableMap<TripType, Int> = mutableMapOf(),

        var sellValues: MutableMap<TripType, Int> = mutableMapOf(),

        var tripTypeProbabilities : MutableMap<TripType, Double> = mutableMapOf()

)