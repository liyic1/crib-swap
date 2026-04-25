package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.ui.components.NotificationToggle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NotificationsScreen(onBack: () -> Unit = {}) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var newMessages    by remember { mutableStateOf(true) }
    var newListings    by remember { mutableStateOf(true) }
    var leaseUpdates   by remember { mutableStateOf(true) }

    var isLoading  by remember { mutableStateOf(true) }
    var saveStatus by remember { mutableStateOf<String?>(null) }

    // Load saved preferences
    LaunchedEffect(uid) {
        if (uid == null) { isLoading = false; return@LaunchedEffect }
        db.collection("users").document(uid)
            .collection("settings").document("notifications")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    newMessages  = doc.getBoolean("newMessages")  ?: true
                    newListings  = doc.getBoolean("newListings")  ?: true
                    leaseUpdates = doc.getBoolean("leaseUpdates") ?: true
                }
                isLoading = false
            }
            .addOnFailureListener { isLoading = false }
    }

    fun save() {
        if (uid == null) return
        saveStatus = null
        db.collection("users").document(uid)
            .collection("settings").document("notifications")
            .set(
                mapOf(
                    "newMessages"  to newMessages,
                    "newListings"  to newListings,
                    "leaseUpdates" to leaseUpdates,
                )
            )
            .addOnSuccessListener { saveStatus = "Saved!" }
            .addOnFailureListener { saveStatus = "Save failed" }
    }

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton { onBack() }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Notifications", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text("Push Notifications", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))

                NotificationToggle("New Messages", newMessages) {
                    newMessages = it; save()
                }
                NotificationToggle("New Listings near me", newListings) {
                    newListings = it; save()
                }
                NotificationToggle("Lease status updates", leaseUpdates) {
                    leaseUpdates = it; save()
                }

                saveStatus?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(it, color = Color(0xFF388E3C), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}