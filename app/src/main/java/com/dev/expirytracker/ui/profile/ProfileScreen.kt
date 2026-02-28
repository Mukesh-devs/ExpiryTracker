package com.dev.expirytracker.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val accentColor = Color(0xFF1565C0)
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )

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
                .padding(top = 16.dp, bottom = 40.dp)
        ) {

            // ── Header ──
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF0D47A1)
            )
            Text(
                text = "Manage your account",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64B5F6)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── User Info Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.1f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user?.email?.substringBefore("@") ?: "User",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF0D47A1)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = user?.email ?: "No email",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF90A4AE)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Change Password Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionLabel("CHANGE PASSWORD")

                        // Forgot password link
                        TextButton(
                            onClick = {
                                user?.email?.let { email ->
                                    auth.sendPasswordResetEmail(email)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                navController.context,
                                                "Reset link sent to $email",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                navController.context,
                                                it.message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                "Reset via Email",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF90A4AE)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = accentColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                currentPasswordVisible = !currentPasswordVisible
                            }) {
                                Icon(
                                    if (currentPasswordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF90A4AE)
                                )
                            }
                        },
                        visualTransformation = if (currentPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.LockReset,
                                contentDescription = null,
                                tint = accentColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                newPasswordVisible = !newPasswordVisible
                            }) {
                                Icon(
                                    if (newPasswordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF90A4AE)
                                )
                            }
                        },
                        visualTransformation = if (newPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        ),
                        supportingText = {
                            if (newPassword.isNotEmpty() && newPassword.length < 6) {
                                Text(
                                    "Minimum 6 characters",
                                    color = Color(0xFFE53935),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (currentPassword.isNotBlank() && newPassword.length >= 6) {
                                isUpdating = true
                                val credential = EmailAuthProvider.getCredential(
                                    user?.email ?: "",
                                    currentPassword
                                )
                                user?.reauthenticate(credential)
                                    ?.addOnSuccessListener {
                                        user.updatePassword(newPassword)
                                            .addOnSuccessListener {
                                                isUpdating = false
                                                Toast.makeText(
                                                    navController.context,
                                                    "Password updated successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                currentPassword = ""
                                                newPassword = ""
                                            }
                                            .addOnFailureListener {
                                                isUpdating = false
                                                Toast.makeText(
                                                    navController.context,
                                                    it.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                    ?.addOnFailureListener {
                                        isUpdating = false
                                        Toast.makeText(
                                            navController.context,
                                            "Current password is incorrect",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 1.dp
                        ),
                        enabled = currentPassword.isNotBlank() && newPassword.length >= 6 && !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                "Update Password",
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Logout Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionLabel("ACCOUNT")
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE53935)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFFE53935).copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Logout",
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── App Info ──
            Text(
                text = "Expiry Tracker v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFBDBDBD),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // ── Logout Confirmation Dialog ──
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
            },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    color = Color(0xFF546E7A)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
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
