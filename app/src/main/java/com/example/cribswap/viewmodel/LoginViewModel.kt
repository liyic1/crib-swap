package com.example.cribswap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cribswap.data.LoginUIEvent
import com.example.cribswap.data.LoginUIState
import com.example.cribswap.data.rules.Validator
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    var loginUIState = mutableStateOf(LoginUIState())
    private val tag = LoginViewModel::class.simpleName
    var allValidatorPassed = mutableStateOf(false)
    var loginInProgress = mutableStateOf(false)

    fun onEvent(event: LoginUIEvent) {
        when(event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email

                )
                printState()
            }
            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }

        }
        validateDataWithRules()
    }

    private fun validateDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )
        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )
        allValidatorPassed.value = emailResult.status && passwordResult.status
    }
    private fun login() {
        val email = loginUIState.value.email
        val password = loginUIState.value.password
        loginInProgress.value = true
        FirebaseAuth //create another class (firebase db) and use this in there
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(tag, "Inside_login_success")
                if (it.isSuccessful) {
                    loginInProgress.value = false
                    CribSwapAppRouter.navigateTo(Screen.MainScreen)
                }
            }
            .addOnFailureListener {
                Log.d(tag, "Inside_login_failure")
                loginInProgress.value = false

            }
    }

    private fun printState() {
        Log.d(tag, "Inside_printState")
        Log.d(tag, loginUIState.value.toString())
    }
}
