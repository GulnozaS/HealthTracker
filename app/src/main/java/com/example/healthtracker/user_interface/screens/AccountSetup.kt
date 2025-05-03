package com.example.healthtracker.user_interface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

// New color definitions

val LightPurple = Color(0xFFD2B4DE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSetupScreen(navController: NavController) {
    // Firebase instances
    val auth = Firebase.auth
    val firestore = Firebase.firestore

    // States for saving process
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Personal Information
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        yearRange = 1900..Calendar.getInstance().get(Calendar.YEAR)
    )

    // Physical Characteristics
    var bloodType by remember { mutableStateOf("") }
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // Medical History
    var allergies by remember { mutableStateOf("") }
    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
    val conditions = listOf("None", "Diabetes", "Hypertension", "Asthma", "Heart Disease")
    var medicationsInput by remember { mutableStateOf("") }

    // Emergency Contact
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val date = Date(timestamp)
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun processListInput(input: String): List<String> {
        return input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun parseMedications(input: String): Map<String, Map<String, String>> {
        return input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .associate { medication ->
                val parts = medication.split("-").map { it.trim() }
                val name = parts.getOrElse(0) { "Unknown" }
                val details = mutableMapOf<String, String>("name" to name)

                if (parts.size >= 2) details["dosage"] = parts[1]
                if (parts.size >= 3) details["schedule"] = parts[2]

                name to details
            }
    }

    fun saveProfile() {
        when {
            fullName.isBlank() -> errorMessage = "Full name is required"
            dateOfBirth.isBlank() -> errorMessage = "Date of birth is required"
            bloodType.isBlank() -> errorMessage = "Blood type is required"
            height.isBlank() -> errorMessage = "Height is required"
            weight.isBlank() -> errorMessage = "Weight is required"
            contactName.isBlank() -> errorMessage = "Emergency contact name is required"
            contactPhone.isBlank() -> errorMessage = "Emergency contact phone is required"
            else -> {
                isLoading = true
                errorMessage = null

                val userId = auth.currentUser?.uid ?: run {
                    errorMessage = "User not authenticated"
                    isLoading = false
                    return
                }

                val userData = hashMapOf(
                    "personalInfo" to hashMapOf(
                        "fullName" to fullName,
                        "dateOfBirth" to dateOfBirth,
                        "gender" to selectedGender
                    ),
                    "physicalCharacteristics" to hashMapOf(
                        "bloodType" to bloodType,
                        "height" to height.toInt(),
                        "weight" to weight.toInt()
                    ),
                    "medicalHistory" to hashMapOf(
                        "allergies" to processListInput(allergies),
                        "chronicConditions" to selectedConditions.filter { it != "None" }.toList(),
                        "medications" to parseMedications(medicationsInput)
                    ),
                    "emergencyContact" to hashMapOf(
                        "name" to contactName,
                        "phone" to contactPhone,
                        "relationship" to relationship
                    ),
                    "profileComplete" to true,
                    "lastUpdated" to FieldValue.serverTimestamp()
                )

                firestore.collection("users").document(userId)
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener {
                        navController.navigate("home") {
                            popUpTo("account_setup") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        errorMessage = "Failed to save: ${e.localizedMessage}"
                        isLoading = false
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Health Profile",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary // Updated color
        )

        Text(
            text = "Complete your profile for personalized health insights",
            fontSize = 16.sp,
            color = Color.Gray
        )

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Personal Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Personal Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fullName.isBlank() && errorMessage != null
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(
                            width = 1.dp,
                            color = if (dateOfBirth.isBlank() && errorMessage != null) MaterialTheme.colorScheme.error else Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = dateOfBirth.ifEmpty { "Select Date of Birth *" },
                        color = if (dateOfBirth.isEmpty()) Color.Gray else Color.Black,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = "Gender *",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedGender == "Male",
                        onClick = { selectedGender = "Male" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary // Updated color
                        )
                    )
                    Text(text = "Male", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedGender == "Female",
                        onClick = { selectedGender = "Female" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary // Updated color
                        )
                    )
                    Text(text = "Female", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedGender == "Other",
                        onClick = { selectedGender = "Other" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary // Updated color
                        )
                    )
                    Text(text = "Other")
                }
            }
        }

        // Physical Characteristics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Physical Characteristics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )

                Text(
                    text = "Blood Type *",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )
                ExposedDropdownMenuBox(
                    expanded = bloodTypeExpanded,
                    onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Select Blood Type *") },
                        isError = bloodType.isBlank() && errorMessage != null
                    )

                    ExposedDropdownMenu(
                        expanded = bloodTypeExpanded,
                        onDismissRequest = { bloodTypeExpanded = false }
                    ) {
                        bloodTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    bloodType = type
                                    bloodTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm) *") },
                        modifier = Modifier.weight(1f),
                        isError = height.isBlank() && errorMessage != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg) *") },
                        modifier = Modifier.weight(1f),
                        isError = weight.isBlank() && errorMessage != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Medical History Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Medical History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("peanuts, penicillin, etc.") }
                )

                Text(
                    text = "Chronic Conditions",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )
                conditions.forEach { condition ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedConditions.contains(condition) ||
                                    (condition == "None" && selectedConditions.isEmpty()),
                            onClick = {
                                if (condition == "None") {
                                    selectedConditions = emptySet()
                                } else {
                                    selectedConditions = if (selectedConditions.contains(condition)) {
                                        selectedConditions - condition
                                    } else {
                                        selectedConditions + condition
                                    }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary // Updated color
                            )
                        )
                        Text(text = condition, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                OutlinedTextField(
                    value = medicationsInput,
                    onValueChange = { medicationsInput = it },
                    label = { Text("Current Medications") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Format: name-dosage-schedule, e.g. ibuprofen-500mg 3x daily-after meals")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
        }

        // Emergency Contact Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Emergency Contact",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Updated color
                )

                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Contact Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = contactName.isBlank() && errorMessage != null
                )

                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("Contact Phone *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = contactPhone.isBlank() && errorMessage != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = { saveProfile() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Updated color
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(text = "Save Profile", fontSize = 16.sp)
            }
        }

        Text(
            text = "Your data is encrypted and protected by HIPAA compliance",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateOfBirth = formatDate(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.primary) // Updated color
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary) // Updated color
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}