package com.example.cribswap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cribswap.data.RegistrationUIState
import com.example.cribswap.data.SignUpUIEvent
import com.example.cribswap.data.rules.Validator
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {
    var registrationUIState = mutableStateOf(RegistrationUIState())
    private val TAG = SignUpViewModel::class.simpleName
    var allValidatorPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)

    fun onEvent(event: SignUpUIEvent) {
        validateDataWithRules()
        when(event) {
            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                printState()
            }
            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is SignUpUIEvent.ReEnterPasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printState()
            }
            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        printState()
//        validateDataWithRules()
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password
        )
    }

    private fun validateDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )
        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "email = $emailResult")
        Log.d(TAG, "password = $passwordResult")
        registrationUIState.value = registrationUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )
        allValidatorPassed.value = emailResult.status && passwordResult.status
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registrationUIState.value.toString())
    }

    private fun createUserInFirebase(email:String, password:String) {
        signUpInProgress.value = true
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG,"Inside_OnCompleteListener")
                Log.d(TAG,"it.isSuccessful = ${it.isSuccessful}")
                signUpInProgress.value = false
                if (it.isSuccessful) {
                    CribSwapAppRouter.navigateTo(Screen.MainScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnFailureListener")
                Log.d(TAG,"Exception = ${it.localizedMessage}")
            }
    }
}