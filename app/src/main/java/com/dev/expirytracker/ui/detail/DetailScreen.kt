package com.dev.expirytracker.ui.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.expirytracker.config.AppConfig
import com.dev.expirytracker.model.ExpiryItem
import com.dev.expirytracker.ui.home.calculateDaysLeft
import com.dev.expirytracker.util.CryptoManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: String,
    navController: NavController
) {

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )
    val accentColor = Color(0xFF1565C0)
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val context = LocalContext.current
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var item by remember { mutableStateOf<ExpiryItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Edit Mode State ──
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editNotes by remember { mutableStateOf("") }
    var editPurchasedDate by remember { mutableStateOf<Long?>(null) }
    var editExpiryDate by remember { mutableStateOf<Long?>(null) }
    var editUsername by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPassword by remember { mutableStateOf("") }
    var editAmount by remember { mutableStateOf("") }
    var showPurchasePicker by remember { mutableStateOf(false) }
    var showExpiryPicker by remember { mutableStateOf(false) }

    fun enterEditMode(data: ExpiryItem) {
        editName = data.name
        editNotes = data.notes
        editPurchasedDate = data.purchasedDate
        editExpiryDate = data.expiryDate
        editUsername = if (data.username.isNotBlank()) CryptoManager.decrypt(data.username) else ""
        editEmail = if (data.email.isNotBlank()) CryptoManager.decrypt(data.email) else ""
        editPassword = if (data.password.isNotBlank()) CryptoManager.decrypt(data.password) else ""
        editAmount = data.amount
        isEditing = true
    }

    LaunchedEffect(Unit) {
        db.collection(AppConfig.USERS_COLLECTION)
            .document(userId)
            .collection(AppConfig.ITEMS_COLLECTION)
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
                    email = it.getString("email") ?: "",
                    password = it.getString("password") ?: "",
                    amount = it.getString("amount") ?: ""
                )
                isLoading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = accentColor,
                    strokeWidth = 3.dp
                )
            }
        }

        item?.let { data ->

            if (isEditing) {
                // ════════════════════════════════════
                // ══       EDIT MODE VIEW          ══
                // ════════════════════════════════════
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 120.dp)
                ) {
                    // ── Header ──
                    Text(
                        text = "Edit Item",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "Update your item details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64B5F6)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Item Details Card ──
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            EditSectionLabel("ITEM DETAILS")
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Item Name") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Inventory2, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Date Row ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EditDateChip(
                                    label = "Purchased",
                                    date = editPurchasedDate?.let { formatter.format(Date(it)) },
                                    accentColor = accentColor,
                                    onClick = { showPurchasePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                                EditDateChip(
                                    label = "Expires",
                                    date = editExpiryDate?.let { formatter.format(Date(it)) },
                                    accentColor = Color(0xFFE53935),
                                    onClick = { showExpiryPicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = editNotes,
                                onValueChange = { editNotes = it },
                                label = { Text("Notes") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Description, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                minLines = 2,
                                maxLines = 4
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Credentials Card ──
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            EditSectionLabel("CREDENTIALS")
                            Text(
                                text = "Optional info",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editUsername,
                                onValueChange = { editUsername = it },
                                label = { Text("Username") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Person, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editEmail,
                                onValueChange = { editEmail = it },
                                label = { Text("Email") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Email, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editPassword,
                                onValueChange = { editPassword = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Lock, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editAmount,
                                onValueChange = { editAmount = it },
                                label = { Text("Amount") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.CurrencyRupee, null, tint = accentColor)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    cursorColor = accentColor
                                ),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }

                // ── Save / Cancel Buttons (floating at bottom) ──
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFFE8F1FC))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isEditing = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (editName.isNotBlank() &&
                                editPurchasedDate != null && editExpiryDate != null
                            ) {
                                // Update local state immediately
                                item = ExpiryItem(
                                    id = itemId,
                                    name = editName,
                                    purchasedDate = editPurchasedDate!!,
                                    expiryDate = editExpiryDate!!,
                                    notes = editNotes,
                                    username = CryptoManager.encrypt(editUsername),
                                    email = CryptoManager.encrypt(editEmail),
                                    password = CryptoManager.encrypt(editPassword),
                                    amount = editAmount
                                )
                                isEditing = false
                                Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()

                                // Firestore update in background
                                val updated = hashMapOf<String, Any>(
                                    "name" to editName,
                                    "purchasedDate" to editPurchasedDate!!,
                                    "expiryDate" to editExpiryDate!!,
                                    "notes" to editNotes,
                                    "username" to CryptoManager.encrypt(editUsername),
                                    "email" to CryptoManager.encrypt(editEmail),
                                    "password" to CryptoManager.encrypt(editPassword),
                                    "amount" to editAmount
                                )

                                db.collection(AppConfig.USERS_COLLECTION)
                                    .document(userId)
                                    .collection(AppConfig.ITEMS_COLLECTION)
                                    .document(itemId)
                                    .update(updated)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 1.dp
                        ),
                        enabled = editName.isNotBlank() && editPurchasedDate != null &&
                                editExpiryDate != null
                    ) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

            } else {
                // ════════════════════════════════════
                // ══       VIEW MODE (existing)    ══
                // ════════════════════════════════════

                val daysLeft = calculateDaysLeft(data.expiryDate)

                val urgencyColor = when {
                    daysLeft <= 0 -> Color(0xFFE53935)
                    daysLeft in 1..10 -> Color(0xFFFFA726)
                    daysLeft in 11..30 -> Color(0xFF42A5F5)
                    else -> Color(0xFF66BB6A)
                }

                val totalDuration = data.expiryDate - data.purchasedDate
                val remaining = data.expiryDate - System.currentTimeMillis()
                val progress = if (totalDuration > 0) {
                    (remaining.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 100.dp)
                ) {

                    // ── Header ──
                    Text(
                        text = data.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "Item details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64B5F6)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Status Banner ──
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = urgencyColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = urgencyColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (daysLeft <= 0) "!" else "$daysLeft",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = urgencyColor
                                        )
                                        if (daysLeft > 0) {
                                            Text(
                                                text = "days",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = urgencyColor
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (daysLeft <= 0) "Expired" else if (daysLeft <= 10) "Expiring Soon" else "Active",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = urgencyColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = urgencyColor,
                                    trackColor = urgencyColor.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Dates Card ──
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            SectionLabel("DATES")
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DateInfoChip(
                                    label = "Purchased",
                                    value = formatDate(data.purchasedDate),
                                    icon = Icons.Outlined.CalendarToday,
                                    color = accentColor,
                                    modifier = Modifier.weight(1f)
                                )
                                DateInfoChip(
                                    label = "Expires",
                                    value = formatDate(data.expiryDate),
                                    icon = Icons.Outlined.Event,
                                    color = urgencyColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ── Notes Card ──
                    if (data.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                SectionLabel("NOTES")
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = data.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF546E7A),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // ── Credentials Card ──
                    val hasCredentials = data.username.isNotBlank() || data.email.isNotBlank() ||
                            data.password.isNotBlank() || data.amount.isNotBlank()

                    if (hasCredentials) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                SectionLabel("CREDENTIALS")
                                Spacer(modifier = Modifier.height(14.dp))

                                if (data.username.isNotBlank()) {
                                    DetailInfoRow(
                                        icon = Icons.Outlined.Person,
                                        label = "Username",
                                        value = CryptoManager.decrypt(data.username),
                                        accentColor = accentColor
                                    )
                                }

                                if (data.email.isNotBlank()) {
                                    DetailInfoRow(
                                        icon = Icons.Outlined.Email,
                                        label = "Email",
                                        value = CryptoManager.decrypt(data.email),
                                        accentColor = accentColor
                                    )
                                }

                                if (data.password.isNotBlank()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = accentColor.copy(alpha = 0.08f),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Outlined.Lock,
                                                    contentDescription = null,
                                                    tint = accentColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Password",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF90A4AE)
                                            )
                                            Text(
                                                text = if (passwordVisible)
                                                    CryptoManager.decrypt(data.password)
                                                else "••••••••",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = Color(0xFF37474F)
                                            )
                                        }
                                        IconButton(
                                            onClick = { passwordVisible = !passwordVisible }
                                        ) {
                                            Icon(
                                                imageVector = if (passwordVisible)
                                                    Icons.Default.Visibility
                                                else
                                                    Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF90A4AE),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                if (data.amount.isNotBlank()) {
                                    DetailInfoRow(
                                        icon = Icons.Outlined.CurrencyRupee,
                                        label = "Amount",
                                        value = data.amount,
                                        accentColor = accentColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // ── FABs: Edit + Delete ──
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit FAB
                    FloatingActionButton(
                        onClick = { enterEditMode(data) },
                        containerColor = accentColor,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }

                    // Delete FAB
                    FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }

        // ── Delete Confirmation Dialog ──
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        "Delete Item",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete this item? This action cannot be undone.",
                        color = Color(0xFF546E7A)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            db.collection(AppConfig.USERS_COLLECTION)
                                .document(userId)
                                .collection(AppConfig.ITEMS_COLLECTION)
                                .document(itemId)
                                .delete()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialog = false },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    // ── Date Pickers for Edit Mode ──
    if (showPurchasePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = editPurchasedDate
        )
        DatePickerDialog(
            onDismissRequest = { showPurchasePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    editPurchasedDate = datePickerState.selectedDateMillis
                    showPurchasePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPurchasePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showExpiryPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = editExpiryDate
        )
        DatePickerDialog(
            onDismissRequest = { showExpiryPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    editExpiryDate = datePickerState.selectedDateMillis
                    showExpiryPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showExpiryPicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

// ── Reusable Components ──

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        ),
        color = Color(0xFF1565C0)
    )
}

@Composable
private fun EditSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        ),
        color = Color(0xFF1565C0)
    )
}

@Composable
private fun EditDateChip(
    label: String,
    date: String?,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = accentColor.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = date ?: "Select",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (date != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (date != null) accentColor else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DateInfoChip(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.08f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF90A4AE)
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF37474F)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}