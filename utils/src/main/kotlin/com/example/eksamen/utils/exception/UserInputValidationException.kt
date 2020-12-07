package com.example.eksamen.utils.exception


class UserInputValidationException(
        message: String,
        val httpCode : Int = 400
) : RuntimeException(message)