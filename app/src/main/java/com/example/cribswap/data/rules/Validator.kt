package com.example.cribswap.data.rules

object Validator {

    fun validateFirstName(firstName: String): ValidationResult {
        return ValidationResult(firstName.isNotEmpty())
    }

    fun validateLastName(lastName: String): ValidationResult {
        return ValidationResult(lastName.isNotEmpty())
    }
    fun validateEmail(email: String) : ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty() && email.contains("@") && email.endsWith(".edu"))
        )
    }

    fun validatePassword(password:String) : ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length>=6)
        )
    }
}

data class ValidationResult(
    val status :Boolean = false
)


