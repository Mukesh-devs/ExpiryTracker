package com.dev.expirytracker.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            // ── Branding ──
            Text(
                text = "Expiry\nTracker",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    lineHeight = 40.sp
                ),
                color = accentColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF90A4AE)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Login Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "SIGN IN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = accentColor
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = accentColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = accentColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF90A4AE)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        )
                    )

                    // Forgot password
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = {
                                if (email.isNotBlank()) {
                                    FirebaseAuth.getInstance()
                                        .sendPasswordResetEmail(email)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                navController.context,
                                                "Reset link sent to email",
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
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Enter your email first",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Text(
                                "Forgot Password?",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF90A4AE)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Login Button ──
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                isLoading = false
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(
                                    navController.context,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
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
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Divider ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Text(
                    text = "  OR  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFBDBDBD)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Register Link ──
            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = accentColor
                )
            ) {
                Text(
                    "Create New Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}