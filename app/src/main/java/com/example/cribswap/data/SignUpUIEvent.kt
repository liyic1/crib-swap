package com.example.cribswap.data

sealed class SignUpUIEvent {
    data class EmailChanged(val email:String) : SignUpUIEvent()
    data class PasswordChanged(val password:String) : SignUpUIEvent()
    data class ReEnterPasswordChanged(val password:String) : SignUpUIEvent()
    object RegisterButtonClicked : SignUpUIEvent()

}