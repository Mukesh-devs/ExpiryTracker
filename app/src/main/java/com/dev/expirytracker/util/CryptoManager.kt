package com.dev.expirytracker.util

import android.util.Base64
import com.dev.expirytracker.config.AppConfig
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoManager {

    private fun getKey(): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = AppConfig.ENCRYPTION_SECRET_KEY.toByteArray(Charsets.UTF_8)
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest().copyOf(16)
        return SecretKeySpec(key, "AES")
    }

    fun encrypt(data: String): String {
        if (data.isBlank()) return ""
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val encrypted = cipher.doFinal(data.toByteArray())
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            data
        }
    }

    fun decrypt(data: String): String {
        if (data.isBlank()) return ""
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, getKey())
            val decoded = Base64.decode(data, Base64.DEFAULT)
            String(cipher.doFinal(decoded))
        } catch (e: Exception) {
            // Return raw data if decryption fails (e.g. plain text or corrupted)
            data
        }
    }
}