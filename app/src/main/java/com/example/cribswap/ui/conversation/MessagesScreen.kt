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
    var selectedConversation by remember { mutableStateOf<ConversationPreview?>(null) }

    if (selectedConversation == null) {
        ConversationPage(
            modifier = modifier,
            onConversationClick = { convo ->
                selectedConversation = convo
            }
        )
    } else {
        ChatPage(
            modifier = modifier,
            userName = selectedConversation!!.name,
            userStatus = "Usually replies quickly",
            onBackClick = {
                selectedConversation = null
            }
        )
    }
}
