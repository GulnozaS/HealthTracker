package com.example.healthtracker.user_interface.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthtracker.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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
                .background(Color(0xFF673AB7))
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
            AppointmentsSection(navController)

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
                    Text("Cancel")
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
private fun AppointmentsSection(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SectionHeader(
            title = "Upcoming Appointments",
            onActionClick = { navController.navigate("appointments") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppointmentCard(
            day = "15",
            doctorName = "Dr. Emily Watson",
            appointmentType = "General Check-up",
            time = "10:00AM"
        )
    }
}

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
                onClick = { /*navController.navigate("reminders")*/ }
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
fun AppointmentCard(
    day: String,
    doctorName: String,
    appointmentType: String,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
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
                    .background(Color(0xFF673AB7)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = appointmentType,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = time,
                fontSize = 14.sp,
                color = Color(0xFF673AB7),
                fontWeight = FontWeight.Medium
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
                    .background(Color(0xFFE1BEE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fire),
                    contentDescription = "Medication",
                    tint = Color(0xFF673AB7),
                    modifier = Modifier.size(24.dp)
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
                    .background(Color(0xFFEDE7F6))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color(0xFF673AB7),
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
                tint = Color(0xFF673AB7),
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

data class HealthSummaryItem(
    val title: String,
    val value: String
)