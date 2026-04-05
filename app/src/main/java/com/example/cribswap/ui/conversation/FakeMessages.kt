package com.example.cribswap.ui.conversation

/**
 * One message inside a chat thread.
 */
data class ChatMessage(
    val id: Int,
    val text: String,
    val time: String,
    val isFromMe: Boolean
)

/**
 * Fake chat messages for UI testing.
 */
val fakeMessages = listOf(
    ChatMessage(
        id = 1,
        text = "blablabla",
        time = "10:36",
        isFromMe = true
    ),
    ChatMessage(
        id = 2,
        text = "blablablalllaaaa.",
        time = "11:22",
        isFromMe = false
    ),
    ChatMessage(
        id = 3,
        text = "blabalbal",
        time = "11:24",
        isFromMe = true
    ),
    ChatMessage(
        id = 4,
        text = "blablabla.",
        time = "11:26",
        isFromMe = false
    ),
    ChatMessage(
        id = 5,
        text = "balblabal",
        time = "11:28",
        isFromMe = true
    )
)