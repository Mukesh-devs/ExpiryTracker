package com.dev.expirytracker.model

data class ExpiryItem(
    val id: String = "",
    val name: String = "",
    val purchasedDate: Long = 0L,
    val expiryDate: Long = 0L,
    val notes: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val amount: String = "",
    val archived: Boolean = false
)