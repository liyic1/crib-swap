package com.example.cribswap.data.repo

import com.example.cribswap.data.model.Message
import com.example.cribswap.data.remote.MessageRemoteDataSource

class MessageRepository(
    private val remoteDataSource: MessageRemoteDataSource,
    private val conversationRepository: ConversationRepository
) {

    suspend fun sendMessage(message: Message) {
        remoteDataSource.sendMessage(message)

        conversationRepository.updateLastMessage(
            conversationId = message.conversationId,
            messageText = message.text
        )
    }

    suspend fun getMessagesForConversation(conversationId: String): List<Message> {
        return remoteDataSource.getMessagesForConversation(conversationId)
    }
}
