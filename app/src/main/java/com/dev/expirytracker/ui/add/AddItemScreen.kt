package com.dev.expirytracker.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    var password by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showPurchasePicker by remember { mutableStateOf(false) }
    var showExpiryPicker by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val backgroundColor = Color(0xFFE1F5FE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {

        Text(
            "Add Expiry Item",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Purchased Date
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = { showPurchasePicker = true },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(260.dp)
                    .height(48.dp)
            ) {
                Text(
                    purchasedDate?.let {
                        "Purchased: ${formatter.format(Date(it))}"
                    } ?: "Select Purchased Date"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expiry Date
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = { showExpiryPicker = true },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(260.dp)
                    .height(48.dp)
            ) {
                Text(
                    expiryDate?.let {
                        "Expiry: ${formatter.format(Date(it))}"
                    } ?: "Select Expiry Date"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Modern Centered Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
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
                            "username" to username,
                            "password" to password
                        )

                        db.collection("users")
                            .document(userId)
                            .collection("expiryItems")
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
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .width(220.dp)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Item", color = Color.White)
                }
            }
        }
    }

    // Purchased Date Picker Dialog
    if (showPurchasePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showPurchasePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    purchasedDate = datePickerState.selectedDateMillis
                    showPurchasePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Expiry Date Picker Dialog
    if (showExpiryPicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showExpiryPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    expiryDate = datePickerState.selectedDateMillis
                    showExpiryPicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}