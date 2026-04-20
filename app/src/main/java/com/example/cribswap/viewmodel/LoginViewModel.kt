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
    var emailOnlyValidatorPassed = mutableStateOf(false) // used by ForgotPasswordScreen
    var loginInProgress = mutableStateOf(false)
    var loginError = mutableStateOf<String?>(null)

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(email = event.email)
            }
            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(password = event.password)
            }
            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateDataWithRules()
    }

    private fun validateDataWithRules() {
        val emailResult = Validator.validateEmail(email = loginUIState.value.email)
        val passwordResult = Validator.validatePassword(password = loginUIState.value.password)

        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

        allValidatorPassed.value = emailResult.status && passwordResult.status
        emailOnlyValidatorPassed.value = emailResult.status
    }

    private fun login() {
        val email = loginUIState.value.email
        val password = loginUIState.value.password
        loginInProgress.value = true
        loginError.value = null

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginInProgress.value = false
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    user?.reload()?.addOnCompleteListener {
                        if (user.isEmailVerified) {
                            CribSwapAppRouter.navigateTo(Screen.MainScreen)
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            loginError.value = "Your email is not verified, please verify before moving on"
                            user.sendEmailVerification()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                loginInProgress.value = false
                loginError.value = e.localizedMessage ?: "Something went wrong"
                Log.d(tag, "Login failure: ${e.localizedMessage}")
            }
    }
}
