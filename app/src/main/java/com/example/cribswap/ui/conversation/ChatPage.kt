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
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChatBlue = Color(0xFF2F80ED)
private val IncomingBubble = Color(0xFFF1F1F1)
private val ScreenBg = Color(0xFFFFFFFF)
private val SubtitleGray = Color(0xFF7A7A7A)
private val BorderGray = Color(0xFFE6E6E6)
private val InputBg = Color(0xFFF9F9F9)

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    userName: String = "Harry Stebbings",
    userStatus: String = "Typically responds within 1 hour",
    onBackClick: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(fakeMessages) } }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
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
                MessageBubble(message = message)
            }
        }

        ChatInputBar(
            value = messageText,
            onValueChange = { messageText = it },
            onSendClick = {
                val trimmed = messageText.trim()
                if (trimmed.isNotEmpty()) {
                    messages.add(
                        ChatMessage(
                            id = messages.size + 1,
                            text = trimmed,
                            time = "Now",
                            isFromMe = true
                        )
                    )
                    messageText = ""
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
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .background(
                        color = if (message.isFromMe) ChatBlue else IncomingBubble,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (message.isFromMe) 18.dp else 4.dp,
                            bottomEnd = if (message.isFromMe) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (message.isFromMe) Color.White else Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )
            }

            Text(
                text = message.time,
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
            modifier = Modifier
                .background(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatPagePreview() {
    MaterialTheme {
        ChatPage()
    }
}
