package com.example.eksamen.usercollections.dto

import io.swagger.annotations.ApiModelProperty

class PatchResultDto (

        @get:ApiModelProperty("If a trip was altered, specify which trips are left")
        var tripIdsInCollectionAfterAlter: MutableList<String> = mutableListOf()
)