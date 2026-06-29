package com.dev.expirytracker.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFE8F1FC))
    )
    val accentColor = Color(0xFF1565C0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {

        // ── App Header ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Expiry Tracker",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Version 3.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF90A4AE)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Never lose track of your subscriptions,\nwarranties, or product expiry dates.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── What is Expiry Tracker ──
        AboutCard(
            title = "What is Expiry Tracker?",
            icon = Icons.Outlined.Info,
            accentColor = accentColor
        ) {
            Text(
                text = "Expiry Tracker helps you keep track of all your items that have an expiry or renewal date — subscriptions, warranties, medicines, food products, memberships, and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A),
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Get a clear overview of what's expiring soon so you never miss a renewal or waste an expired product.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A),
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── How to Use ──
        AboutCard(
            title = "How to Use",
            icon = Icons.Outlined.TouchApp,
            accentColor = accentColor
        ) {
            StepItem(
                number = "1",
                title = "Add an Item",
                description = "Tap the + button on the home screen. Enter the item name, purchase date, and expiry date. Optionally add notes.",
                accentColor = accentColor
            )
            StepItem(
                number = "2",
                title = "Add Credentials (Optional)",
                description = "For subscriptions or accounts, tap the + inside the credentials section to save username, email, password, or amount. These are encrypted and stored securely.",
                accentColor = accentColor
            )
            StepItem(
                number = "3",
                title = "Track Your Items",
                description = "The home screen shows all your items sorted by urgency. Color-coded badges and progress bars tell you at a glance what needs attention.",
                accentColor = accentColor
            )
            StepItem(
                number = "4",
                title = "Edit or Delete",
                description = "Tap any item to see full details. Use the blue edit button to update info, or the red delete button to remove it.",
                accentColor = accentColor
            )
            StepItem(
                number = "5",
                title = "Swipe Expired Items",
                description = "Expired items show a \"swipe to remove\" hint. Swipe left to move them to the Expired Items archive. You can restore or permanently delete them from there.",
                accentColor = accentColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Features ──
        AboutCard(
            title = "Features",
            icon = Icons.Outlined.Star,
            accentColor = accentColor
        ) {
            FeatureItem(
                icon = Icons.Outlined.Dashboard,
                title = "Smart Dashboard",
                description = "See Expired, Urgent, and Safe counts at a glance with color-coded summary chips.",
                accentColor = accentColor
            )
            FeatureItem(
                icon = Icons.Outlined.Edit,
                title = "Edit Items",
                description = "Update any item's details, dates, or credentials anytime with the built-in edit mode.",
                accentColor = accentColor
            )
            FeatureItem(
                icon = Icons.Outlined.SwipeLeft,
                title = "Swipe to Archive",
                description = "Swipe expired items left to move them to the archive. Keep your home screen clean.",
                accentColor = accentColor
            )
            FeatureItem(
                icon = Icons.Outlined.Restore,
                title = "Restore Items",
                description = "Accidentally archived? Restore items from the Expired Items screen anytime.",
                accentColor = accentColor
            )
            FeatureItem(
                icon = Icons.Outlined.Refresh,
                title = "Real-time Sync",
                description = "Your data syncs across devices using cloud storage. Changes appear instantly.",
                accentColor = accentColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Security & Encryption ──
        AboutCard(
            title = "Your Data is Encrypted",
            icon = Icons.Outlined.Shield,
            accentColor = Color(0xFF2E7D32)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2E7D32).copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "All sensitive data is encrypted using AES-256 encryption before being stored.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF2E7D32),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "We take your privacy seriously. Here's how your data is protected:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecurityPoint(
                title = "Encrypted Credentials",
                description = "Your usernames, emails, and passwords are encrypted with AES encryption before being saved. Even if someone accesses the database, they cannot read your credentials.",
                icon = Icons.Outlined.Key
            )
            SecurityPoint(
                title = "Secure Cloud Storage",
                description = "Your data is stored in Google Cloud with authentication. Only you can access your items — no one else, not even the developer.",
                icon = Icons.Outlined.Cloud
            )
            SecurityPoint(
                title = "No Plain Text Passwords",
                description = "Passwords are never stored as plain text. They are encrypted before leaving your device and decrypted only when you view them.",
                icon = Icons.Outlined.VisibilityOff
            )
            SecurityPoint(
                title = "Per-User Isolation",
                description = "Each user's data is stored in a separate, isolated collection. Your items are only accessible when logged in with your account.",
                icon = Icons.Outlined.Person
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Color Codes ──
        AboutCard(
            title = "Understanding Colors",
            icon = Icons.Outlined.Palette,
            accentColor = accentColor
        ) {
            Text(
                text = "Items are color-coded based on how close they are to expiring:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A),
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            ColorCodeRow(
                color = Color(0xFFE53935),
                label = "Red — Expired",
                description = "The item has already expired."
            )
            ColorCodeRow(
                color = Color(0xFFFFA726),
                label = "Orange — Urgent",
                description = "Expiring within the next 10 days."
            )
            ColorCodeRow(
                color = Color(0xFF42A5F5),
                label = "Blue — Moderate",
                description = "Expiring within 11–30 days."
            )
            ColorCodeRow(
                color = Color(0xFF66BB6A),
                label = "Green — Safe",
                description = "More than 30 days remaining."
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Footer ──
        Text(
            text = "Author - Mukesh \n Any Query - contact@mukesh.tech",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB0BEC5),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ── Reusable Components ──

@Composable
private fun AboutCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
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
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color(0xFF0D47A1)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun StepItem(
    number: String,
    title: String,
    description: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78909C),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = accentColor.copy(alpha = 0.08f),
            modifier = Modifier.size(36.dp)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78909C),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SecurityPoint(
    title: String,
    description: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.08f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78909C),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ColorCodeRow(
    color: Color,
    label: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(14.dp)
        ) {}
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF37474F)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF90A4AE)
            )
        }
    }
}


