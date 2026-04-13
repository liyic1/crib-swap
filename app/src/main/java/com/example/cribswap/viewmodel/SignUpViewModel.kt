package com.example.cribswap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cribswap.data.RegistrationUIState
import com.example.cribswap.data.SignUpUIEvent
import com.example.cribswap.data.model.User
import com.example.cribswap.data.repo.UserRepository
import com.example.cribswap.data.rules.Validator
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpViewModel : ViewModel() {
    var registrationUIState = mutableStateOf(RegistrationUIState())
    private val tag = SignUpViewModel::class.simpleName
    var allValidatorPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)
    var signUpError = mutableStateOf<String?>(null)

    fun onEvent(event: SignUpUIEvent) {
        when (event) {
            is SignUpUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(firstName = event.firstName)
            }
            is SignUpUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(lastName = event.lastName)
            }
            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(email = event.email)
            }
            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(password = event.password)
            }
            is SignUpUIEvent.ReEnterPasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(rePassword = event.password)
            }
            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
        validateDataWithRules()
    }

    private fun validateDataWithRules() {
        val firstNameResult = Validator.validateFirstName(registrationUIState.value.firstName)
        val lastNameResult = Validator.validateLastName(registrationUIState.value.lastName)
        val emailResult = Validator.validateEmail(registrationUIState.value.email)
        val passwordResult = Validator.validatePassword(registrationUIState.value.password)

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = firstNameResult.status,
            lastNameError = lastNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

        allValidatorPassed.value = firstNameResult.status && lastNameResult.status &&
                emailResult.status && passwordResult.status
    }

    private fun signUp() {
        signUpInProgress.value = true
        signUpError.value = null

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(
                registrationUIState.value.email,
                registrationUIState.value.password
            )
            .addOnCompleteListener { task ->
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnCompleteListener
                    val fullName = "${registrationUIState.value.firstName} ${registrationUIState.value.lastName}"

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)

                    val newUser = User(
                        uid = uid,
                        email = registrationUIState.value.email,
                        displayName = fullName,
                        firstName = registrationUIState.value.firstName,
                        lastName = registrationUIState.value.lastName
                    )
                    UserRepository.createUser(
                        user = newUser,
                        onSuccess = {
                            Log.d(tag, "User saved to Firestore successfully")
                            CribSwapAppRouter.navigateTo(Screen.MainScreen)
                        },
                        onFailure = { e ->
                            Log.d(tag, "Firestore save failed: ${e.message}")
                            CribSwapAppRouter.navigateTo(Screen.MainScreen)
                        }
                    )
                } else {
                    signUpError.value = task.exception?.localizedMessage ?: "Sign up failed"
                    Log.d(tag, "SignUp failed: ${task.exception?.message}")
                }
            }
            .addOnFailureListener { e ->
                signUpInProgress.value = false
                signUpError.value = e.localizedMessage ?: "Something went wrong"
                Log.d(tag, "SignUp failure: ${e.localizedMessage}")
            }
    }
}