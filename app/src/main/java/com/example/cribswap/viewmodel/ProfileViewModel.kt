package com.example.cribswap.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cribswap.data.model.User
import com.example.cribswap.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    var user = mutableStateOf<User?>(null)
    var isLoading = mutableStateOf(true)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        loadUser()
    }

    fun loadUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            isLoading.value = false
            errorMessage.value = "Not logged in"
            return
        }
        isLoading.value = true
        UserRepository.getUser(
            uid = uid,
            onSuccess = { fetchedUser ->
                user.value = fetchedUser
                isLoading.value = false
            },
            onFailure = { e ->
                errorMessage.value = e.localizedMessage
                isLoading.value = false
            }
        )
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}