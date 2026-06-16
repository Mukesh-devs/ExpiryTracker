package com.dev.expirytracker.model

import com.google.firebase.Timestamp

data class ExpiryItem(
    val id: String = "",
    val itemName: String = "",
    val purchasedDate: Timestamp? = null,
    val expiryDate: Timestamp? = null,
    val amount: Double? = null,
    val notes: String = "",

    // Plain text credentials
    val username: String = "",
    val email: String = "",
    val password: String = "",

    val ownerId: String = "",
    val sharedWith: List<String> = emptyList(),
    val archived: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)