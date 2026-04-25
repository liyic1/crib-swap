package com.example.cribswap.ui.conversation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier
) {
    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    var selectedUserName by remember { mutableStateOf("") }

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
                selectedConversationId = null
                selectedUserName = ""
            }
        )
    }
}