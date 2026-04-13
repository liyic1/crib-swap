package com.example.cribswap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cribswap.data.model.User
import com.example.cribswap.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
class ProfileViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)
        private set

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private val tag = ProfileViewModel::class.simpleName

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            errorMessage.value = "No user logged in"
            isLoading.value = false
            return
        }

        UserRepository.getUser(
            uid = uid,
            onSuccess = { user ->
                currentUser.value = user
                isLoading.value = false
                Log.d(tag, "User fetched: ${user?.displayName}")
            },
            onFailure = { e ->
                errorMessage.value = "Failed to load profile"
                isLoading.value = false
                Log.d(tag, "Failed to fetch user: ${e.message}")
            }
        )
    }

    fun getDisplayName(): String {
        val user = currentUser.value
        if (user == null) {
            return "Loading..."
        }
        if (user.displayName.isEmpty()) {
            return "No name set"
        }
        return user.displayName
    }
}