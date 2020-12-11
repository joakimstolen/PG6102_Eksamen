package com.example.eksamen.utils.exception
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/rest/rest-exception/src/main/kotlin/org/tsdes/advanced/rest/exception/UserInputValidationException.kt

class UserInputValidationException(
        message: String,
        val httpCode : Int = 400
) : RuntimeException(message)