package com.example.healthtracker.user_interface.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthtracker.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    var showLogoutDialog by remember { mutableStateOf(false) }
    var userData by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch user data from Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                userData = document.toObject(UserData::class.java)?.apply {
                    // Set default values if any fields are null
                    if (physicalCharacteristics.height.toString().isEmpty()) {
                        physicalCharacteristics = physicalCharacteristics.copy(height = 0)
                    }
                    if (physicalCharacteristics.weight.toString().isEmpty()) {
                        physicalCharacteristics = physicalCharacteristics.copy(weight = 0)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load data. Please try again."
                Log.e("HomeScreen", "Error fetching user data", e)
            } finally {
                isLoading = false
            }
        }
    }

    // Loading state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HealthTracker",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome back,",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Text(
                    text = userData?.personalInfo?.fullName ?: "User",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Error message display
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            // Health Summary Section
            HealthSummarySection(
                height = (userData?.physicalCharacteristics?.height ?: "--").toString(),
                weight = (userData?.physicalCharacteristics?.weight ?: "--").toString(),
                bloodType = userData?.physicalCharacteristics?.bloodType ?: "Unknown"
            )

            // Appointments Section
            AppointmentsSection(navController, userData)

            // Medication Reminders Section
            MedicationRemindersSection(
                medications = userData?.medicalHistory?.medications ?: emptyMap(),
                navController = navController
            )

            // Quick Actions Section
            QuickActionsSection(navController)
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
private fun HealthSummarySection(
    height: String,
    weight: String,
    bloodType: String
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SectionHeader(title = "Health Summary")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HealthSummaryCard(HealthSummaryItem("Height", "$height cm"))
            HealthSummaryCard(HealthSummaryItem("Weight", "$weight kg"))
            HealthSummaryCard(HealthSummaryItem("Blood Type", bloodType))
        }
    }
}

@Composable
private fun AppointmentsSection(navController: NavController, userData: UserData?) {
    val firestore = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""

    var upcomingAppointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch upcoming appointments
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            isLoading = true
            try {
                val now = Calendar.getInstance().time
                val querySnapshot = firestore.collection("appointments")
                    .whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("date", now)
                    .orderBy("date")
                    .get()
                    .await()

                upcomingAppointments = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Appointment::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load appointments"
                Log.e("HomeScreen", "Error fetching appointments", e)
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SectionHeader(
            title = "Calorie Recommendation"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display calories if available
        if (userData?.nutrition?.dailyCalories ?: 0 > 0) {
            DailyCaloriesCard(
                calories = userData?.nutrition?.dailyCalories ?: 0
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = "Upcoming Appointments",
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            upcomingAppointments.isEmpty() -> {
                Text(
                    text = "No upcoming appointments",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    upcomingAppointments.forEach { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = { /* Handle click if needed */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit = {}
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dateFormatter.format(appointment.date).take(6), // Shows "May 15"
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.doctorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = appointment.purpose,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = timeFormatter.format(appointment.time),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Add this data class if you don't have it already
data class Appointment(
    val id: String = "",
    val userId: String = "",
    val purpose: String = "",
    val doctorName: String = "",
    val date: Date = Date(),
    val time: Date = Date(),
    val createdAt: Date = Date(),
    val status: String = "pending"
)

@Composable
private fun MedicationRemindersSection(
    medications: Map<String, Medication>,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SectionHeader(
            title = "Medication Reminders",
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (medications.isEmpty()) {
            Text(
                text = "No medications added yet",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            medications.forEach { (name, details) ->
                MedicationCard(
                    name = name,
                    dosage = details.dosage,
                    time = details.schedule,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavController) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SectionHeader(title = "Quick Actions")

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionButton(
                text = "Set Reminder",
                iconRes = R.drawable.reminder,
                onClick = { navController.navigate("set_reminder") }
            )

            QuickActionButton(
                text = "History",
                iconRes = R.drawable.calendar,
                onClick = { navController.navigate("history") }
            )

            QuickActionButton(
                text = "AI Health",
                iconRes = R.drawable.code,
                onClick = { navController.navigate("ai") }
            )

            QuickActionButton(
                text = "Support",
                iconRes = R.drawable.chat,
                onClick = { navController.navigate("helpcenter") }
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        actionText?.let {
            Text(
                text = actionText,
                color = Color(0xFF673AB7),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
fun HealthSummaryCard(item: HealthSummaryItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MedicationCard(
    name: String,
    dosage: String,
    time: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = dosage,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color =MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DailyCaloriesCard(
    calories: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Your Daily Calorie Goal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$calories kcal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fire), // Use your calorie icon
                    contentDescription = "Calories",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class HealthSummaryItem(
    val title: String,
    val value: String
)