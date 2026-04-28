package com.example.cribswap.ui.conversation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cribswap.data.repo.ConversationRepository

@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier,
    startWithUserId: String? = null,
    startWithUserName: String? = null
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val conversationRepository = remember { ConversationRepository(db, auth) }

    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    var selectedUserName by remember { mutableStateOf("") }

    // If coming from a listing, auto-open chat with that seller
    LaunchedEffect(startWithUserId) {
        if (startWithUserId != null) {
            val conversationId = conversationRepository
                .getOrCreateConversation(
                    otherUserId = startWithUserId,
                    listingId = null
                )
            selectedConversationId = conversationId
            selectedUserName = startWithUserName ?: "Seller"
        }
    }

    if (selectedConversationId == null) {
        ConversationPage(
            modifier = modifier,
            onConversationClick = { conversationId, userName ->
                selectedConversationId = conversationId
                selectedUserName = userName
            }
        )
    } else {
        ChatPage(
            modifier = modifier,
            conversationId = selectedConversationId!!,
            userName = selectedUserName,
            userStatus = "Usually replies quickly",
            onBackClick = {
                if (startWithUserId != null) {
                    // came from listing — go back to conversation list
                    selectedConversationId = null
                } else {
                    selectedConversationId = null
                }
                selectedUserName = ""
            }
        )
    }
}