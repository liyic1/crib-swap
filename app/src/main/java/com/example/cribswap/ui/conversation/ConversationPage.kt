package com.example.cribswap.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cribswap.data.model.Conversation
import com.example.cribswap.data.repo.ConversationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

private val AccentBlue = Color(0xFF2196F3)
private val PageBackground = Color(0xFFFFFFFF)

@Composable
fun ConversationPage(
    modifier: Modifier = Modifier,

    // These are optional values from a listing page.
    // When the user taps the message icon on a listing,
    // your teammate can pass the listing owner/seller info here.
    startOtherUserId: String? = null,
    startOtherUserName: String? = null,
    startListingId: String? = null,

    onNewChatClick: () -> Unit = {},

    // Sends the selected conversation id and display name to MessagesScreen/ChatPage.
    onConversationClick: (String, String) -> Unit = { _, _ -> }
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val conversationRepository = remember { ConversationRepository(db, auth) }
    val conversations = remember { mutableStateListOf<Conversation>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(startOtherUserId, startListingId) {
        if (startOtherUserId != null) {
            val conversationId = conversationRepository.getOrCreateConversation(
                otherUserId = startOtherUserId,
                listingId = startListingId
            )

            onConversationClick(
                conversationId,
                startOtherUserName ?: startOtherUserId
            )
        }

        conversations.clear()
        conversations.addAll(conversationRepository.getConversation())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(top = 18.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Messages",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "${conversations.size} conversations",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        val conversationId = conversationRepository.getOrCreateConversation(
                            otherUserId = "test-seller-1",
                            listingId = "test-listing-1"
                        )

                        conversations.clear()
                        conversations.addAll(conversationRepository.getConversation())

                        onConversationClick(
                            conversationId,
                            "Test Seller"
                        )
                    }

                    onNewChatClick()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFEAF3FF))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Create new chat",
                    tint = AccentBlue
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(conversations) { conversation ->
                val myUid = auth.currentUser?.uid ?: "test-buyer-1"

                val otherUserId =
                    conversation.participants.firstOrNull { it != myUid } ?: "Unknown User"

                val preview = ConversationPreview(
                    name = otherUserId,
                    lastMessage = conversation.lastMessage ?: "",
                    time = formatConversationTime(conversation.lastMessageAt?.toDate()?.time),
                    unreadCount = 0
                )

                ConversationRow(
                    convo = preview,
                    onClick = {
                        onConversationClick(conversation.id, otherUserId)
                    }
                )
            }
        }
    }
}

private fun formatConversationTime(timestamp: Long?): String {
    if (timestamp == null) return ""

    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(timestamp)
}

@Preview(showBackground = true)
@Composable
fun ConversationPagePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            ConversationPage()
        }
    }
}