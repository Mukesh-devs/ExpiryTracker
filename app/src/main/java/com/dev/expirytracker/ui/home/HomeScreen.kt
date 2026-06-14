package com.dev.expirytracker.ui.home

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SwipeLeft
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )

    var myItems by remember { mutableStateOf(listOf<ExpiryItem>()) }
    var sharedItems by remember { mutableStateOf(listOf<ExpiryItem>()) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Items, 1: Shared

    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    val displayedItems = if (selectedTab == 0) myItems else sharedItems

    fun loadItems() {
        // Fetch current user's username
        db.collection(AppConfig.USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val currentUserUsername = userDoc.getString("username") ?: ""

                // Fetch My Items
                db.collection(AppConfig.USERS_COLLECTION)
                    .document(userId)
                    .collection(AppConfig.ITEMS_COLLECTION)
                    .addSnapshotListener { result, _ ->
                        result?.let {
                            myItems = it.documents.mapNotNull { doc ->
                                val archived = doc.getBoolean("archived") ?: false
                                if (archived) return@mapNotNull null
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
                                    ownerId = doc.getString("ownerId") ?: userId,
                                    ownerUsername = doc.getString("ownerUsername") ?: "",
                                    sharedWith = doc.get("sharedWith") as? List<String> ?: emptyList(),
                                    archived = false
                                )
                            }.sortedBy { calculateDaysLeft(it.expiryDate) }
                            isLoading = false
                            isRefreshing = false
                        }
                    }

                // Fetch Shared Items (By username)
                if (currentUserUsername.isNotEmpty()) {
                    db.collectionGroup(AppConfig.ITEMS_COLLECTION)
                        .whereArrayContains("sharedWith", currentUserUsername)
                        .whereEqualTo("archived", false)
                        .addSnapshotListener { result, _ ->
                            result?.let {
                                sharedItems = it.documents.map { doc ->
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
                                        ownerId = doc.getString("ownerId") ?: "",
                                        ownerUsername = doc.getString("ownerUsername") ?: "",
                                        sharedWith = doc.get("sharedWith") as? List<String> ?: emptyList(),
                                        archived = false
                                    )
                                }.sortedBy { calculateDaysLeft(it.expiryDate) }
                            }
                        }
                }
            }
    }

    LaunchedEffect(Unit) {
        loadItems()
    }

    val onRefresh: () -> Unit = {
        scope.launch {
            isRefreshing = true
            loadItems()
            delay(800)
            isRefreshing = false
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

            // ── Header Section ──
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Expiry Tracker",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                text = if (selectedTab == 0) 
                                    "${myItems.size} item${if (myItems.size != 1) "s" else ""} owned"
                                else 
                                    "${sharedItems.size} item${if (sharedItems.size != 1) "s" else ""} shared",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64B5F6)
                            )
                        }
                        FilledTonalIconButton(
                            onClick = onRefresh,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF1565C0).copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                Icons.Outlined.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFF1565C0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Tab Switcher ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "My Items",
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Shared",
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Loading State ──
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
            if (!isLoading && displayedItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFBBDEFB)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedTab == 0) "No items yet" else "No shared items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF90CAF9)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedTab == 0) 
                                "Tap + to add your first expiry item"
                            else 
                                "Items shared with you will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }

            // ── Item Cards ──
            items(displayedItems, key = { it.id }) { item ->
                val isExpired = calculateDaysLeft(item.expiryDate) <= 0

                // Only allow swipe to archive for owned items
                if (isExpired && selectedTab == 0) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                // Archive the expired item
                                db.collection(AppConfig.USERS_COLLECTION)
                                    .document(userId)
                                    .collection(AppConfig.ITEMS_COLLECTION)
                                    .document(item.id)
                                    .update("archived", true)
                                Toast.makeText(
                                    context,
                                    "${item.name} moved to Expired Items",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val scale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.8f else 1.2f,
                                label = "scale"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE53935))
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Outlined.DeleteSweep,
                                    contentDescription = "Archive",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .scale(scale)
                                )
                            }
                        }
                    ) {
                        ItemCard(
                            item = item,
                            onClick = { navController.navigate("detail/${item.id}/${item.ownerId}") },
                            isExpired = true
                        )
                    }
                } else {
                    ItemCard(
                        item = item,
                        onClick = { navController.navigate("detail/${item.id}/${item.ownerId}") },
                        isExpired = isExpired
                    )
                }
            }
        }

        // Refresh indicator
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color(0xFF1565C0),
                trackColor = Color(0xFF1565C0).copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF1565C0) else Color.Transparent,
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF1565C0),
        label = "textColor"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )
        }
    }
}

// ── Item Card ──
@Composable
private fun ItemCard(item: ExpiryItem, onClick: () -> Unit, isExpired: Boolean = false) {

    val daysLeft = calculateDaysLeft(item.expiryDate)

    val urgencyColor = when {
        daysLeft <= 0 -> Color(0xFFE53935)
        daysLeft in 1..10 -> Color(0xFFFFA726)
        daysLeft in 11..30 -> Color(0xFF42A5F5)
        else -> Color(0xFF66BB6A)
    }

    val totalDuration = item.expiryDate - item.purchasedDate
    val remaining = item.expiryDate - System.currentTimeMillis()
    val progress = if (totalDuration > 0) {
        (remaining.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progress"
    )

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
                // Left: Name + dates
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
                        text = "${formatDate(item.purchasedDate)}  →  ${formatDate(item.expiryDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        letterSpacing = 0.2.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right: Days badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = urgencyColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = urgencyColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (daysLeft <= 0) "Expired" else "${daysLeft}d",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = urgencyColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = urgencyColor,
                trackColor = urgencyColor.copy(alpha = 0.12f)
            )

            // Notes preview
            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBDBDBD),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Swipe hint for expired items (only for owned items)
            if (isExpired) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "swipe to remove",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = Color(0xFFE57373).copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        Icons.Outlined.SwipeLeft,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFE57373).copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
