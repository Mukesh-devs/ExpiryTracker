package com.dev.expirytracker.config

import com.dev.expirytracker.BuildConfig

/**
 * Centralized app configuration.
 * All sensitive keys are read from BuildConfig (injected from local.properties).
 */
object AppConfig {

    // Firestore collection names
    val USERS_COLLECTION: String = BuildConfig.FIRESTORE_USERS_COLLECTION
    val ITEMS_COLLECTION: String = BuildConfig.FIRESTORE_ITEMS_COLLECTION

    // Encryption secret key
    val ENCRYPTION_SECRET_KEY: String = BuildConfig.ENCRYPTION_SECRET_KEY
}

