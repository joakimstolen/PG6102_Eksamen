package com.example.eksamen.trip.dto

class TripCollectionDto (

        var trips : MutableList<TripDto> = mutableListOf(),

        var prices: MutableMap<TripType, Int> = mutableMapOf(),

        var sellValues: MutableMap<TripType, Int> = mutableMapOf(),

        var tripTypeProbabilities : MutableMap<TripType, Double> = mutableMapOf()

)