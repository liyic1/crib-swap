package com.example.cribswap.ui.conversation

import androidx.compose.ui.graphics.Color

/**
 * UI model for one conversation row.
 */
data class ConversationPreview(
    val name: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val profileColor: Color = Color(0xFFE0E0E0)
)

/**
 * Fake placeholder data for the conversation list.
 */
val fakeConvo = listOf(
    ConversationPreview(
        name = "john doe",
        lastMessage = "jhfbjsbdjfbhsf",
        time = "2:45 PM",
        unreadCount = 2,
        profileColor = Color(0xFFDDEBFF)
    ),
    ConversationPreview(
        name = "jane doe",
        lastMessage = "HAHHAHHAHHAHA",
        time = "1:10 PM",
        unreadCount = 1,
        profileColor = Color(0xFFE8EAF6)
    ),
    ConversationPreview(
        name = "Bob",
        lastMessage = "NOOOOOOO",
        time = "Yesterday",
        unreadCount = 0,
        profileColor = Color(0xFFFFE0E0)
    ),
    ConversationPreview(
        name = "Joe",
        lastMessage = "YAYYYYYY",
        time = "Mon",
        unreadCount = 3,
        profileColor = Color(0xFFE0F7FA)
    ),
    ConversationPreview(
        name = "Layla",
        lastMessage = "JHBJHCBSDJBJS",
        time = "Sun",
        unreadCount = 0,
        profileColor = Color(0xFFF3E5F5)
    ),
    ConversationPreview(
        name = "father",
        lastMessage = "BJBSJCBSBHSIUDH.",
        time = "Sat",
        unreadCount = 0,
        profileColor = Color(0xFFFFF3E0)
    )
)