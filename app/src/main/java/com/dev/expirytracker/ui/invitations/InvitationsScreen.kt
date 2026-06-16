package com.dev.expirytracker.ui.invitations

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.expirytracker.model.ShareInvitation
import com.dev.expirytracker.service.SharingService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvitationsScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharingService = remember { SharingService(context) }
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )

    var invitations by remember { mutableStateOf(listOf<ShareInvitation>()) }
    var isLoading by remember { mutableStateOf(true) }
    var processingId by remember { mutableStateOf<String?>(null) }

    // Listen for pending invitations
    LaunchedEffect(Unit) {
        db.collection(SharingService.SHARES_COLLECTION)
            .whereEqualTo("toUserId", userId)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                result?.let {
                    invitations = it.documents.map { doc ->
                        ShareInvitation(
                            id = doc.id,
                            itemId = doc.getString("itemId") ?: "",
                            itemName = doc.getString("itemName") ?: "Unknown",
                            fromUserId = doc.getString("fromUserId") ?: "",
                            fromUserEmail = doc.getString("fromUserEmail") ?: "",
                            toUserId = doc.getString("toUserId") ?: "",
                            toUserEmail = doc.getString("toUserEmail") ?: "",
                            status = doc.getString("status") ?: "pending",
                            createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                        )
                    }.sortedByDescending { it.createdAt.toDate().time }
                    isLoading = false
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Invitations",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color(0xFF0D47A1)
                )
                Text(
                    text = "${invitations.size} pending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64B5F6)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Loading
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1565C0),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            // Empty
            if (!isLoading && invitations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.MailOutline,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFBBDEFB)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No invitations",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF90CAF9)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Share invitations will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }

            // Invitation Cards
            items(invitations, key = { it.id }) { invite ->
                val isProcessing = processingId == invite.id
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1565C0).copy(alpha = 0.1f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = null,
                                        tint = Color(0xFF1565C0),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = invite.itemName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color(0xFF0D47A1)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "From: ${invite.fromUserEmail}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF78909C)
                                )
                                Text(
                                    text = dateFormat.format(invite.createdAt.toDate()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB0BEC5)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (isProcessing) return@OutlinedButton
                                    processingId = invite.id
                                    scope.launch {
                                        val result = sharingService.declineInvitation(invite.id)
                                        processingId = null
                                        result.onSuccess {
                                            Toast.makeText(context, "Invitation declined", Toast.LENGTH_SHORT).show()
                                        }.onFailure {
                                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFE53935)
                                ),
                                enabled = !isProcessing
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFFE53935)
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Decline", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isProcessing) return@Button
                                    processingId = invite.id
                                    scope.launch {
                                        val result = sharingService.acceptInvitation(invite.id)
                                        processingId = null
                                        result.onSuccess {
                                            Toast.makeText(context, "Invitation accepted!", Toast.LENGTH_SHORT).show()
                                        }.onFailure {
                                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                enabled = !isProcessing
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Accept", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}