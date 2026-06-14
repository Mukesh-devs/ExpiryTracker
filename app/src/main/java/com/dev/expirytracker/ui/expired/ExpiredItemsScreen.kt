package com.dev.expirytracker.ui.expired

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.expirytracker.config.AppConfig
import com.dev.expirytracker.model.ExpiryItem
import com.dev.expirytracker.ui.home.calculateDaysLeft
import com.dev.expirytracker.ui.home.formatDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ExpiredItemsScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val context = LocalContext.current

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )

    var items by remember { mutableStateOf(listOf<ExpiryItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection(AppConfig.USERS_COLLECTION)
            .document(userId)
            .collection(AppConfig.ITEMS_COLLECTION)
            .addSnapshotListener { result, _ ->
                result?.let {
                    val list = it.documents.mapNotNull { doc ->
                        val archived = doc.getBoolean("archived") ?: false
                        if (archived) {
                            ExpiryItem(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                purchasedDate = doc.getLong("purchasedDate") ?: 0L,
                                expiryDate = doc.getLong("expiryDate") ?: 0L,
                                notes = doc.getString("notes") ?: "",
                                username = doc.getString("username") ?: "",
                                email = doc.getString("email") ?: "",
                                password = doc.getString("password") ?: "",
                                amount = doc.getString("amount") ?: "",
                                archived = true,
                                ownerId = doc.getString("ownerId") ?: userId,
                                sharedWith = doc.get("sharedWith") as? List<String> ?: emptyList()
                            )
                        } else null
                    }
                    items = list.sortedByDescending { it.expiryDate }
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
                top = 12.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header ──
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Expired Items",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "${items.size} archived item${if (items.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64B5F6)
                    )
                }
            }

            // ── Loading ──
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

            // ── Empty State ──
            if (!isLoading && items.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFBBDEFB)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No expired items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF90CAF9)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Swiped expired items will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }

            // ── Expired Item Cards ──
            items(items, key = { it.id }) { item ->
                ExpiredItemCard(
                    item = item,
                    onRestore = {
                        db.collection(AppConfig.USERS_COLLECTION)
                            .document(userId)
                            .collection(AppConfig.ITEMS_COLLECTION)
                            .document(item.id)
                            .update("archived", false)
                        Toast.makeText(context, "${item.name} restored", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        db.collection(AppConfig.USERS_COLLECTION)
                            .document(userId)
                            .collection(AppConfig.ITEMS_COLLECTION)
                            .document(item.id)
                            .delete()
                        Toast.makeText(context, "${item.name} deleted", Toast.LENGTH_SHORT).show()
                    },
                    onClick = { navController.navigate("detail/${item.id}/${item.ownerId}") }
                )
            }
        }
    }
}

@Composable
private fun ExpiredItemCard(
    item: ExpiryItem,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val daysExpired = -calculateDaysLeft(item.expiryDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.3).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF1A237E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expired on ${formatDate(item.expiryDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE53935).copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFE53935)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${daysExpired}d ago",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1565C0)
                    )
                ) {
                    Icon(
                        Icons.Outlined.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(
                        Icons.Outlined.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }
        }
    }
}


