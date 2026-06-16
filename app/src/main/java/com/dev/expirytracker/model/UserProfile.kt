package com.dev.expirytracker.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Timestamp = Timestamp.now()
)