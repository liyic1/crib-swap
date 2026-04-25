package com.example.cribswap.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cribswap.data.model.Message
import com.example.cribswap.data.remote.MessageRemoteDataSource
import com.example.cribswap.data.repo.ConversationRepository
import com.example.cribswap.data.repo.MessageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

private val ChatBlue = Color(0xFF2F80ED)
private val IncomingBubble = Color(0xFFF1F1F1)
private val ScreenBg = Color(0xFFFFFFFF)
private val SubtitleGray = Color(0xFF7A7A7A)
private val BorderGray = Color(0xFFE6E6E6)
private val InputBg = Color(0xFFF9F9F9)

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    conversationId: String,
    userName: String,
    userStatus: String = "Typically responds within 1 hour",
    onBackClick: () -> Unit = {}
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val conversationRepository = remember { ConversationRepository(db, auth) }
    val messageRemoteDataSource = remember { MessageRemoteDataSource(db) }
    val messageRepository = remember {
        MessageRepository(messageRemoteDataSource, conversationRepository)
    }

    val messages = remember { mutableStateListOf<Message>() }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    suspend fun loadMessages() {
        messages.clear()
        messages.addAll(messageRepository.getMessagesForConversation(conversationId))
    }

    LaunchedEffect(conversationId) {
        loadMessages()
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .navigationBarsPadding()
    ) {
        ChatTopBar(
            userName = userName,
            userStatus = userStatus,
            onBackClick = onBackClick
        )

        HorizontalDivider(color = BorderGray)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ChatDayLabel(label = "Today")
            }

            items(messages) { message ->
                MessageBubble(
                    text = message.text,
                    time = formatMessageTime(message.timestamp),
                    isFromMe = message.senderId == auth.currentUser?.uid
                )
            }
        }

        ChatInputBar(
            value = messageText,
            onValueChange = { messageText = it },
            onSendClick = {
                val trimmed = messageText.trim()
                val myUid = auth.currentUser?.uid ?: return@ChatInputBar

                if (trimmed.isNotEmpty()) {
                    scope.launch {
                        val newMessage = Message(
                            conversationId = conversationId,
                            senderId = myUid,
                            text = trimmed,
                            timestamp = Timestamp.now(),
                            read = false
                        )

                        messageRepository.sendMessage(newMessage)
                        messageText = ""
                        loadMessages()

                        if (messages.isNotEmpty()) {
                            listState.scrollToItem(messages.lastIndex)
                        }
                    }
                }
            }
        )

        Spacer(
            modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime)
        )
    }
}

@Composable
private fun ChatTopBar(
    userName: String,
    userStatus: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = ChatBlue
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFDDEBFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(),
                color = ChatBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = userStatus,
                fontSize = 13.sp,
                color = SubtitleGray
            )
        }
    }
}

@Composable
private fun ChatDayLabel(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFF7F7F7),
            shape = RoundedCornerShape(999.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MessageBubble(
    text: String,
    time: String,
    isFromMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .background(
                        color = if (isFromMe) ChatBlue else IncomingBubble,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isFromMe) 18.dp else 4.dp,
                            bottomEnd = if (isFromMe) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = text,
                    color = if (isFromMe) Color.White else Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )
            }

            Text(
                text = time,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = BorderGray)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text("Message")
            },
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.size(8.dp))

        IconButton(
            onClick = onSendClick,
            modifier = Modifier.background(
                color = if (value.isNotBlank()) ChatBlue else Color(0xFFBFD7FA),
                shape = CircleShape
            )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

private fun formatMessageTime(timestamp: Timestamp): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(timestamp.toDate())
}
