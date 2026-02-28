package com.dev.expirytracker.ui.add

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev.expirytracker.config.AppConfig
import com.dev.expirytracker.util.CryptoManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var purchasedDate by remember { mutableStateOf<Long?>(null) }
    var expiryDate by remember { mutableStateOf<Long?>(null) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    var showUsername by remember { mutableStateOf(false) }
    var showEmail by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showAmount by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var showPurchasePicker by remember { mutableStateOf(false) }
    var showExpiryPicker by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )
    val accentColor = Color(0xFF1565C0)
    val cardColor = Color.White

    val hasAnyCredential = showUsername || showEmail || showPassword || showAmount
    val allCredentialsAdded = showUsername && showEmail && showPassword && showAmount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 120.dp)
        ) {

            // ── Header ──
            Text(
                text = "New Item",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF0D47A1)
            )
            Text(
                text = "Track your product expiry",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5C6BC0).copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Item Details Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    SectionLabel("ITEM DETAILS")
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = accentColor
                            )
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
                        DateChip(
                            label = "Purchased",
                            date = purchasedDate?.let { formatter.format(Date(it)) },
                            accentColor = accentColor,
                            onClick = { showPurchasePicker = true },
                            modifier = Modifier.weight(1f)
                        )
                        DateChip(
                            label = "Expires",
                            date = expiryDate?.let { formatter.format(Date(it)) },
                            accentColor = Color(0xFFE53935),
                            onClick = { showExpiryPicker = true },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Notes,
                                contentDescription = null,
                                tint = accentColor
                            )
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

            // ── Credentials Section ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            SectionLabel("CREDENTIALS")
                            Text(
                                text = "Optional info",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        if (!allCredentialsAdded) {
                            Box {
                                FilledIconButton(
                                    onClick = { showDropdown = true },
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = accentColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add credential",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showDropdown,
                                    onDismissRequest = { showDropdown = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    if (!showUsername) {
                                        CredentialDropdownItem(
                                            icon = Icons.Outlined.Person,
                                            label = "Username",
                                            onClick = {
                                                showUsername = true
                                                showDropdown = false
                                            }
                                        )
                                    }
                                    if (!showEmail) {
                                        CredentialDropdownItem(
                                            icon = Icons.Outlined.Email,
                                            label = "Email",
                                            onClick = {
                                                showEmail = true
                                                showDropdown = false
                                            }
                                        )
                                    }
                                    if (!showPassword) {
                                        CredentialDropdownItem(
                                            icon = Icons.Outlined.Lock,
                                            label = "Password",
                                            onClick = {
                                                showPassword = true
                                                showDropdown = false
                                            }
                                        )
                                    }
                                    if (!showAmount) {
                                        CredentialDropdownItem(
                                            icon = Icons.Outlined.CurrencyRupee,
                                            label = "Amount",
                                            onClick = {
                                                showAmount = true
                                                showDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!hasAnyCredential) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFBBDEFB),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showDropdown = true }
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFFBBDEFB),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Tap + to add credentials",
                                    color = Color(0xFF90CAF9),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // ── Individual Credential Fields ──
                    CredentialField(
                        visible = showUsername,
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        icon = Icons.Outlined.Person,
                        accentColor = accentColor,
                        onRemove = {
                            showUsername = false
                            username = ""
                        }
                    )

                    CredentialField(
                        visible = showEmail,
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = Icons.Outlined.Email,
                        accentColor = accentColor,
                        onRemove = {
                            showEmail = false
                            email = ""
                        }
                    )

                    CredentialField(
                        visible = showPassword,
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Outlined.Lock,
                        accentColor = accentColor,
                        onRemove = {
                            showPassword = false
                            password = ""
                        }
                    )

                    CredentialField(
                        visible = showAmount,
                        value = amount,
                        onValueChange = { amount = it },
                        label = "Amount",
                        icon = Icons.Outlined.CurrencyRupee,
                        accentColor = accentColor,
                        onRemove = {
                            showAmount = false
                            amount = ""
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // ── Save Button (Floating at bottom) ──
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFE8F1FC).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 32.dp, vertical = 20.dp)
                .padding(bottom = 60.dp)
        ) {
            Button(
                onClick = {
                    if (!isSaving && name.isNotBlank()
                        && purchasedDate != null && expiryDate != null
                    ) {
                        isSaving = true

                        val item = hashMapOf(
                            "name" to name,
                            "purchasedDate" to purchasedDate,
                            "expiryDate" to expiryDate,
                            "notes" to notes,
                            "username" to CryptoManager.encrypt(username),
                            "email" to CryptoManager.encrypt(email),
                            "password" to CryptoManager.encrypt(password),
                            "amount" to amount
                        )

                        db.collection(AppConfig.USERS_COLLECTION)
                            .document(userId)
                            .collection(AppConfig.ITEMS_COLLECTION)
                            .add(item)
                            .addOnSuccessListener {
                                isSaving = false
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                isSaving = false
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                ),
                enabled = name.isNotBlank() && purchasedDate != null && expiryDate != null && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        "Save Item",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    // ── Date Pickers ──
    if (showPurchasePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPurchasePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    purchasedDate = datePickerState.selectedDateMillis
                    showPurchasePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPurchasePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showExpiryPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showExpiryPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    expiryDate = datePickerState.selectedDateMillis
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
private fun DateChip(
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
        color = accentColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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
private fun CredentialDropdownItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(label)
            }
        },
        onClick = onClick
    )
}

@Composable
private fun CredentialField(
    visible: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onRemove: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                leadingIcon = {
                    Icon(icon, contentDescription = null, tint = accentColor)
                },
                trailingIcon = {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove $label",
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
}