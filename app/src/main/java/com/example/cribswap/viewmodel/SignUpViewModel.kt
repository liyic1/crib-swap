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

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val tag = "SignUpViewModel"

    var registrationUIState = mutableStateOf(RegistrationUIState())
        private set

    var allValidatorPassed = mutableStateOf(false)
        private set

    var signUpInProgress = mutableStateOf(false)
        private set

    var signUpError = mutableStateOf<String?>(null)
        private set

    fun onEvent(event: SignUpUIEvent) {
        when (event) {
            is SignUpUIEvent.FirstNameChanged -> {
                registrationUIState.value =
                    registrationUIState.value.copy(firstName = event.firstName)
            }

            is SignUpUIEvent.LastNameChanged -> {
                registrationUIState.value =
                    registrationUIState.value.copy(lastName = event.lastName)
            }

            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value =
                    registrationUIState.value.copy(email = event.email)
            }

            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value =
                    registrationUIState.value.copy(password = event.password)
            }

            is SignUpUIEvent.ReEnterPasswordChanged -> {
                registrationUIState.value =
                    registrationUIState.value.copy(rePassword = event.password)
            }

            is SignUpUIEvent.RegisterButtonClicked -> {
                validateDataWithRules()
                if (allValidatorPassed.value) {
                    signUp()
                } else {
                    signUpError.value = "Please fix the highlighted fields."
                }
            }
        }

        if (event !is SignUpUIEvent.RegisterButtonClicked) {
            validateDataWithRules()
        }
    }

    private fun validateDataWithRules() {
        val state = registrationUIState.value

        val firstNameResult = Validator.validateFirstName(state.firstName)
        val lastNameResult = Validator.validateLastName(state.lastName)
        val emailResult = Validator.validateEmail(state.email)
        val passwordResult = Validator.validatePassword(state.password)
        val rePasswordResult = ValidationResult(
            status = state.password.isNotEmpty() &&
                    state.rePassword.isNotEmpty() &&
                    state.password == state.rePassword
        )

        registrationUIState.value = state.copy(
            firstNameError = firstNameResult.status,
            lastNameError = lastNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            rePasswordError = rePasswordResult.status
        )

        allValidatorPassed.value =
            firstNameResult.status &&
                    lastNameResult.status &&
                    emailResult.status &&
                    passwordResult.status &&
                    rePasswordResult.status
    }

    private fun signUp() {
        val state = registrationUIState.value
        val email = state.email.trim()
        val password = state.password
        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()
        val fullName = "$firstName $lastName".trim()

        signUpInProgress.value = true
        signUpError.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (!createTask.isSuccessful) {
                    signUpInProgress.value = false
                    signUpError.value =
                        createTask.exception?.localizedMessage ?: "Sign up failed."
                    Log.e(tag, "createUserWithEmailAndPassword failed", createTask.exception)
                    return@addOnCompleteListener
                }

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    signUpInProgress.value = false
                    signUpError.value = "User was created, but no authenticated user was found."
                    Log.e(tag, "Firebase currentUser is null after sign up")
                    return@addOnCompleteListener
                }

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener { profileTask ->
                        if (!profileTask.isSuccessful) {
                            Log.w(tag, "Failed to update display name", profileTask.exception)
                        }

                        firebaseUser.sendEmailVerification()
                            .addOnCompleteListener { emailTask ->
                                if (!emailTask.isSuccessful) {
                                    signUpInProgress.value = false
                                    signUpError.value =
                                        emailTask.exception?.localizedMessage
                                            ?: "Failed to send verification email."
                                    Log.e(tag, "sendEmailVerification failed", emailTask.exception)
                                    return@addOnCompleteListener
                                }

                                val newUser = User(
                                    uid = firebaseUser.uid,
                                    email = email,
                                    displayName = fullName,
                                    firstName = firstName,
                                    lastName = lastName
                                )

                                UserRepository.createUser(
                                    user = newUser,
                                    onSuccess = {
                                        signUpInProgress.value = false
                                        Log.d(tag, "User saved to Firestore")
                                        CribSwapAppRouter.navigateTo(Screen.EmailVerificationScreen)
                                    },
                                    onFailure = { e ->
                                        signUpInProgress.value = false
                                        signUpError.value =
                                            e.localizedMessage
                                                ?: "Account created, but failed to save profile."
                                        Log.e(tag, "Firestore save failed", e)
                                    }
                                )
                            }
                    }
            }
            .addOnFailureListener { e ->
                signUpInProgress.value = false
                signUpError.value = e.localizedMessage ?: "Something went wrong."
                Log.e(tag, "Sign up failed", e)
            }
    }

    private data class ValidationResult(val status: Boolean)
}