package com.example.healthtracker.user_interface.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SetReminderScreen(navController: NavController) {
    // State variables
    var purpose by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf<Date?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Formatters
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Firebase
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val colors = MaterialTheme.colorScheme

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = calendar.time
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 40.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Set Appointment Reminder",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Never miss your medical appointments",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        Divider(color = colors.outline.copy(alpha = 0.3f), thickness = 1.dp)

        // Input Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Purpose of visit") },
                placeholder = { Text("e.g., Annual checkup") },
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            OutlinedTextField(
                value = doctorName,
                onValueChange = { doctorName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Doctor's name") },
                placeholder = { Text("e.g., Dr. Smith") },
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            // Date Picker Button
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(
                    text = selectedDate?.let { dateFormatter.format(it) } ?: "Select Date",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Time Picker Button
            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(
                    text = selectedTime?.let { timeFormatter.format(it) } ?: "Select Time",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (validateInput(purpose, doctorName, selectedDate, selectedTime)) {
                        saveAppointment(
                            db = db,
                            purpose = purpose,
                            doctorName = doctorName,
                            date = selectedDate!!,
                            time = selectedTime!!,
                            onLoading = { isLoading = it },
                            onSuccess = { navController.popBackStack() },

                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                enabled = validateInput(purpose, doctorName, selectedDate, selectedTime)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Save Appointment",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// Helper functions remain the same as before
private fun validateInput(
    purpose: String,
    doctorName: String,
    date: Date?,
    time: Date?
): Boolean {
    println(purpose.isNotBlank() && doctorName.isNotBlank() && date != null && time != null)
    return purpose.isNotBlank() && doctorName.isNotBlank() && date != null && time != null
}

private fun saveAppointment(
    db: FirebaseFirestore,
    purpose: String,
    doctorName: String,
    date: Date,
    time: Date,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit
) {
    onLoading(true)

    val appointment = hashMapOf(
        "purpose" to purpose,
        "doctorName" to doctorName,
        "date" to date,
        "time" to time,
        "createdAt" to Date()
    )

    db.collection("appointments")
        .add(appointment)
        .addOnSuccessListener {
            onLoading(false)
            onSuccess()
        }
        .addOnFailureListener {
            onLoading(false)
            // Handle error
        }
}