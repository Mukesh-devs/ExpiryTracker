package com.dev.expirytracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dev.expirytracker.model.ExpiryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun calculateDaysLeft(expiryDate: Long): Long {
    val diff = expiryDate - System.currentTimeMillis()
    return TimeUnit.MILLISECONDS.toDays(diff)
}

@Composable
fun HomeScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE1F5FE),
            Color(0xFFB3E5FC)
        )
    )

    var items by remember { mutableStateOf(listOf<ExpiryItem>()) }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(userId)
            .collection("expiryItems")
            .addSnapshotListener { result, _ ->

                result?.let {
                    val list = it.documents.map { doc ->
                        ExpiryItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            purchasedDate = doc.getLong("purchasedDate") ?: 0L,
                            expiryDate = doc.getLong("expiryDate") ?: 0L,
                            notes = doc.getString("notes") ?: "",
                            username = doc.getString("username") ?: "",
                            password = doc.getString("password") ?: ""
                        )
                    }

                    items = list.sortedBy { calculateDaysLeft(it.expiryDate) }
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp // small safe space above bottom bar
        )
    ) {

        items(items) { item ->

            val daysLeft = calculateDaysLeft(item.expiryDate)

            val urgencyColor = when {
                daysLeft <= 10 -> Color.Red
                daysLeft in 11..20 -> Color(0xFFFF9800)
                else -> Color(0xFF4CAF50)
            }

            val totalDuration = item.expiryDate - item.purchasedDate
            val remaining = item.expiryDate - System.currentTimeMillis()

            val progress = if (totalDuration > 0) {
                (remaining.toFloat() / totalDuration.toFloat())
                    .coerceIn(0f, 1f)
            } else 0f

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("detail/${item.id}")
                    },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Days Left: $daysLeft",
                            color = urgencyColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = progress,
                            color = urgencyColor,
                            trackColor = urgencyColor.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(horizontalAlignment = Alignment.End) {

                        Text(
                            text = formatDate(item.purchasedDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formatDate(item.expiryDate),
                            color = urgencyColor
                        )
                    }
                }
            }
        }
    }
}