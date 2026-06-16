package com.dev.expirytracker.service

import android.content.Context
import android.widget.Toast
import com.dev.expirytracker.model.ShareInvitation
import com.dev.expirytracker.model.UserProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SharingService(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser get() = auth.currentUser

    companion object {
        const val USERS_COLLECTION = "users"
        const val ITEMS_COLLECTION = "items"
        const val SHARES_COLLECTION = "shares"
    }

    // ═══════════════════════════════════════════════════════
    // STEP 1: Search Recipient by Email
    // ═══════════════════════════════════════════════════════
    suspend fun searchRecipientByEmail(email: String): Result<UserProfile> {
        return try {
            val trimmedEmail = email.trim().lowercase()

            val snapshot = db.collection(USERS_COLLECTION)
                .whereEqualTo("email", trimmedEmail)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("User not found"))
            }

            val doc = snapshot.documents.first()
            val userProfile = UserProfile(
                uid = doc.getString("uid") ?: "",
                email = doc.getString("email") ?: "",
                displayName = doc.getString("displayName") ?: "",
                createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
            )

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // STEP 2 & 3: Share Item (Send Invitation)
    // ═══════════════════════════════════════════════════════
    // Creates a pending invitation. Does NOT add recipient to sharedWith yet.
    // Recipient must ACCEPT before gaining access.
    suspend fun shareItem(itemId: String, recipientEmail: String): Result<String> {
        val currentUserId = currentUser?.uid
            ?: return Result.failure(Exception("Not authenticated"))
        val currentUserEmail = currentUser?.email
            ?: return Result.failure(Exception("Email not available"))

        return try {
            // 1. Search recipient
            val recipientResult = searchRecipientByEmail(recipientEmail)
            if (recipientResult.isFailure) {
                return Result.failure(Exception("User not found with email: $recipientEmail"))
            }
            val recipient = recipientResult.getOrThrow()

            // 2. Validate
            val itemRef = db.collection(ITEMS_COLLECTION).document(itemId)
            val itemDoc = itemRef.get().await()

            if (!itemDoc.exists()) {
                return Result.failure(Exception("Item not found"))
            }

            val ownerId = itemDoc.getString("ownerId") ?: ""
            val sharedWith = itemDoc.get("sharedWith") as? List<String> ?: emptyList()
            val itemName = itemDoc.getString("itemName") ?: "Unknown Item"

            // Check 2: Can't share with self
            if (recipient.uid == currentUserId) {
                return Result.failure(Exception("Cannot share with yourself"))
            }

            // Check 4: Only owner can share
            if (ownerId != currentUserId) {
                return Result.failure(Exception("Only the owner can share this item"))
            }

            // Check 5: Not already shared (accepted)
            if (recipient.uid in sharedWith) {
                return Result.failure(Exception("Already shared with this user"))
            }

            // Check 6: No pending invitation already exists
            val existingInvite = db.collection(SHARES_COLLECTION)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("toUserId", recipient.uid)
                .whereEqualTo("status", "pending")
                .limit(1)
                .get()
                .await()

            if (!existingInvite.isEmpty) {
                return Result.failure(Exception("Pending invitation already exists"))
            }

            // 3. Create pending invitation ONLY
            // Do NOT add to sharedWith yet — recipient must accept first
            val shareId = UUID.randomUUID().toString()
            val shareRef = db.collection(SHARES_COLLECTION).document(shareId)

            val shareData = hashMapOf(
                "id" to shareId,
                "itemId" to itemId,
                "itemName" to itemName,
                "fromUserId" to currentUserId,
                "fromUserEmail" to currentUserEmail,
                "toUserId" to recipient.uid,
                "toUserEmail" to recipient.email,
                "status" to "pending",
                "createdAt" to Timestamp.now()
            )
            shareRef.set(shareData).await()

            Result.success(shareId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // Accept Invitation
    // ═══════════════════════════════════════════════════════
    // Adds recipient to sharedWith and updates status to "accepted"
    suspend fun acceptInvitation(shareId: String): Result<Unit> {
        val currentUserId = currentUser?.uid
            ?: return Result.failure(Exception("Not authenticated"))

        return try {
            val shareRef = db.collection(SHARES_COLLECTION).document(shareId)
            val shareDoc = shareRef.get().await()

            if (!shareDoc.exists()) {
                return Result.failure(Exception("Invitation not found"))
            }

            val toUserId = shareDoc.getString("toUserId") ?: ""
            val itemId = shareDoc.getString("itemId") ?: ""
            val status = shareDoc.getString("status") ?: ""

            // Validation: current user must be the recipient
            if (toUserId != currentUserId) {
                return Result.failure(Exception("Not authorized to accept this invitation"))
            }

            // Must be pending
            if (status != "pending") {
                return Result.failure(Exception("Invitation is already $status"))
            }

            // Atomic: Add to sharedWith + update status to accepted
            val itemRef = db.collection(ITEMS_COLLECTION).document(itemId)

            db.runTransaction { transaction ->
                transaction.update(itemRef, "sharedWith", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                transaction.update(shareRef, "status", "accepted")
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // Decline Invitation
    // ═══════════════════════════════════════════════════════
    // Deletes the share document. Does NOT add to sharedWith.
    suspend fun declineInvitation(shareId: String): Result<Unit> {
        val currentUserId = currentUser?.uid
            ?: return Result.failure(Exception("Not authenticated"))

        return try {
            val shareRef = db.collection(SHARES_COLLECTION).document(shareId)
            val shareDoc = shareRef.get().await()

            if (!shareDoc.exists()) {
                return Result.failure(Exception("Invitation not found"))
            }

            val toUserId = shareDoc.getString("toUserId") ?: ""

            // Validation: current user must be the recipient
            if (toUserId != currentUserId) {
                return Result.failure(Exception("Not authorized to decline this invitation"))
            }

            // Delete the invitation
            shareRef.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // Revoke Access (Owner)
    // ═══════════════════════════════════════════════════════
    // Removes user from sharedWith and deletes related shares
    suspend fun revokeAccess(itemId: String, userIdToRemove: String): Result<Unit> {
        val currentUserId = currentUser?.uid
            ?: return Result.failure(Exception("Not authenticated"))

        return try {
            val itemRef = db.collection(ITEMS_COLLECTION).document(itemId)
            val itemDoc = itemRef.get().await()

            if (!itemDoc.exists()) {
                return Result.failure(Exception("Item not found"))
            }

            val ownerId = itemDoc.getString("ownerId") ?: ""

            // Validation: only owner can revoke
            if (ownerId != currentUserId) {
                return Result.failure(Exception("Only the owner can revoke access"))
            }

            // Atomic: Remove from sharedWith
            db.runTransaction { transaction ->
                transaction.update(itemRef, "sharedWith", com.google.firebase.firestore.FieldValue.arrayRemove(userIdToRemove))
            }.await()

            // Delete related share documents (any status)
            val sharesQuery = db.collection(SHARES_COLLECTION)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("toUserId", userIdToRemove)
                .get()
                .await()

            val batch = db.batch()
            sharesQuery.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // Get Pending Invitations for Current User
    // ═══════════════════════════════════════════════════════
    fun getPendingInvitations() =
        db.collection(SHARES_COLLECTION)
            .whereEqualTo("toUserId", currentUser?.uid ?: "")
            .whereEqualTo("status", "pending")

    // ═══════════════════════════════════════════════════════
    // Get Accepted Shared Items (for current user)
    // ═══════════════════════════════════════════════════════
    fun getSharedItems() =
        db.collection(ITEMS_COLLECTION)
            .whereArrayContains("sharedWith", currentUser?.uid ?: "")
            .whereEqualTo("archived", false)
}