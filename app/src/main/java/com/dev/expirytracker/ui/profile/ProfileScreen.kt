package com.dev.expirytracker.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var resetMessage by remember { mutableStateOf<String?>(null) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFE1F5FE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {

        // 🔹 Email Section
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Email",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user?.email ?: "No Email",
            style = MaterialTheme.typography.bodyLarge
        )

        Divider(modifier = Modifier.padding(vertical = 24.dp))

        // 🔹 Change Password Section
        Text(
            text = "Change Password",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Forgot Password
        TextButton(
            onClick = {
                user?.email?.let {
                    auth.sendPasswordResetEmail(it).addOnSuccessListener {
                        resetMessage = "Password reset link sent. Please check inbox or spam."
                    }
                        .addOnSuccessListener {
                            Toast.makeText(
                                navController.context,
                                "Reset link sent to email",
                                Toast.LENGTH_LONG
                            ).show()
                        }.addOnFailureListener {
                            resetMessage = "Failed to send reset link."
                        }
                }
            }
        ) {
            Text("Forgot Password?")
        }
        resetMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color(0xFF2E7D32), // Green
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Update Password Button
        Button(
            onClick = {

                if (currentPassword.isNotBlank() && newPassword.isNotBlank()) {

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
                                        "Password Updated Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    currentPassword = ""
                                    newPassword = ""
                                }
                                .addOnFailureListener {
                                    isUpdating = false
                                }
                        }
                        ?.addOnFailureListener {
                            isUpdating = false
                            Toast.makeText(
                                navController.context,
                                "Current Password Incorrect",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(220.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0)
            )
        ) {
            if (isUpdating) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Update Password", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔹 Logout Button (Bottom)
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}