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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2196F3)
private val PageBackground = Color(0xFFFFFFFF)

@Composable
fun ConversationPage(
    modifier: Modifier = Modifier,
    onNewChatClick: () -> Unit = {},
    onConversationClick: (ConversationPreview) -> Unit = {}
) {
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
                    text = "${fakeConvo.size} conversations",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onNewChatClick,
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
            items(fakeConvo) { convo ->
                ConversationRow(
                    convo = convo,
                    onClick = {
                        onConversationClick(convo)
                    }
                )
            }
        }
    }
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