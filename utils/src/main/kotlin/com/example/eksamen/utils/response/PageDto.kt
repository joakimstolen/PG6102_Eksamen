package com.example.eksamen.utils.response

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.jetbrains.annotations.NotNull

@ApiModel(description = "Paginated list of resources")
class PageDto<T>(

        @ApiModelProperty("The data contained in the page")
        @get:NotNull
        var list: List<T> = listOf(),

        @ApiModelProperty("Link to the next page, if it exists")
        var next: String? = null
)