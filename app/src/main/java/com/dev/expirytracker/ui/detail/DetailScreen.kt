package com.dev.expirytracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dev.expirytracker.model.ExpiryItem
import com.dev.expirytracker.ui.home.calculateDaysLeft
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailScreen(
    itemId: String,
    navController: NavController
) {

    val backgroundColor = Color(0xFFE1F5FE)
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    var item by remember { mutableStateOf<ExpiryItem?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(userId)
            .collection("expiryItems")
            .document(itemId)
            .get()
            .addOnSuccessListener {

                item = ExpiryItem(
                    id = it.id,
                    name = it.getString("name") ?: "",
                    purchasedDate = it.getLong("purchasedDate") ?: 0L,
                    expiryDate = it.getLong("expiryDate") ?: 0L,
                    notes = it.getString("notes") ?: "",
                    username = it.getString("username") ?: "",
                    password = it.getString("password") ?: ""
                )
            }
    }

    item?.let {

        val daysLeft = calculateDaysLeft(it.expiryDate)

        val urgencyColor = when {
            daysLeft <= 10 -> Color.Red
            daysLeft in 11..20 -> Color(0xFFFF9800)
            else -> Color(0xFF4CAF50)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
                    .padding(bottom = 100.dp) // 🔥 important: avoid bottom bar overlap
            ) {

                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Dates")
                Spacer(modifier = Modifier.height(8.dp))

                DetailRow("Purchased", formatDate(it.purchasedDate))
                DetailRow("Expires", formatDate(it.expiryDate), urgencyColor)
                DetailRow("Days Left", "$daysLeft", urgencyColor)

                Divider(modifier = Modifier.padding(vertical = 20.dp))

                SectionTitle("Notes")
                Spacer(modifier = Modifier.height(8.dp))
                Text(it.notes)

                if (it.username.isNotBlank() || it.password.isNotBlank()) {

                    Divider(modifier = Modifier.padding(vertical = 20.dp))

                    SectionTitle("Credentials")
                    Spacer(modifier = Modifier.height(8.dp))

                    if (it.username.isNotBlank()) {
                        DetailRow("Username", it.username)
                    }

                    if (it.password.isNotBlank()) {

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = if (passwordVisible)
                                    it.password
                                else
                                    "••••••••",
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            // 🔴 DELETE FAB (Properly Positioned)
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp) // 🔥 pushed above bottom bar
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }

        // CONFIRMATION DIALOG
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Item") },
                text = { Text("Are you sure you want to delete this item?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            db.collection("users")
                                .document(userId)
                                .collection("expiryItems")
                                .document(itemId)
                                .delete()
                                .addOnSuccessListener {
                                    showDialog = false
                                    navController.popBackStack()
                                }
                        }
                    ) {
                        Text("Yes", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Black) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, color = valueColor)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}