package com.dev.expirytracker.model

data class ExpiryItem(
    val id: String = "",
    val name: String = "",
    val purchasedDate: Long = 0L,
    val expiryDate: Long = 0L,
    val notes: String = "",
    val username: String = "", // This is for encrypted credentials username
    val email: String = "",    // This is for encrypted credentials email
    val password: String = "",
    val amount: String = "",
    val archived: Boolean = false,
    val ownerId: String = "",
    val ownerUsername: String = "", // Added: Username of the owner
    val sharedWith: List<String> = emptyList() // Now stores usernames
)