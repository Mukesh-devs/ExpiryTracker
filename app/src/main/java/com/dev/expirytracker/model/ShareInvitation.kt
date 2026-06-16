package com.dev.expirytracker.model

import com.google.firebase.Timestamp

data class ShareInvitation(
    val id: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val fromUserId: String = "",
    val fromUserEmail: String = "",
    val toUserId: String = "",
    val toUserEmail: String = "",
    val status: String = "pending", // pending, accepted, declined, revoked
    val createdAt: Timestamp = Timestamp.now()
)