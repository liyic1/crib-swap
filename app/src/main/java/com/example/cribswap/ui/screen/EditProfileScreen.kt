package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.data.repo.UserRepository
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user = profileViewModel.user.value

    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName  by remember { mutableStateOf(user?.lastName  ?: "") }
    var phone     by remember { mutableStateOf(user?.phoneNumber ?: "") }
    var address   by remember { mutableStateOf(user?.address ?: "") }

    var isSaving   by remember { mutableStateOf(false) }
    var saveResult by remember { mutableStateOf<String?>(null) }
    var isError    by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton { onBack() }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Edit Profile", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(24.dp))

            saveResult?.let { msg ->
                Text(
                    text = msg,
                    color = if (isError) Color.Red else Color(0xFF388E3C),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    isSaving = true
                    saveResult = null
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                    val fullName = "$firstName $lastName"

                    // Update Firebase Auth display name
                    val updates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    FirebaseAuth.getInstance().currentUser?.updateProfile(updates)

                    // Update Firestore
                    val fields = mapOf(
                        "firstName"   to firstName,
                        "lastName"    to lastName,
                        "displayName" to fullName,
                        "phoneNumber" to phone,
                        "address"     to address
                    )
                    UserRepository.updateUser(
                        uid = uid,
                        fields = fields,
                        onSuccess = {
                            isSaving = false
                            isError = false
                            saveResult = "Profile updated!"
                            profileViewModel.loadUser()
                        },
                        onFailure = { e ->
                            isSaving = false
                            isError = true
                            saveResult = e.localizedMessage ?: "Update failed"
                        }
                    )
                },
                enabled = !isSaving && firstName.isNotBlank() && lastName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}