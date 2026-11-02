package com.example.daliachat

/**
 * Representa un mensaje de la conversación.
 * Permite distinguir mensajes propios, recibidos y mensajes con imágenes.
 */
data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isImage: Boolean = false,
    val isMine: Boolean = false
)
