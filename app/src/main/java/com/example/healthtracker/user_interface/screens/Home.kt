package com.example.healthtracker.user_interface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthtracker.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .verticalScroll(rememberScrollState())
    ) {
        // Header with welcome message
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF673AB7))
                .padding(16.dp)
        ) {
            Button(onClick = { Firebase.auth.signOut() }) {}
            Text(
                text = "HomePageOverall",
                color = Color.White,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back,",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "Sarah Johnson",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Health Summary section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Health Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(healthSummaryItems) { item ->
                    HealthSummaryCard(item)
                }
            }
        }

        // Upcoming Appointments section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Appointments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "See All",
                    color = Color(0xFF673AB7),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* Navigate to appointments */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppointmentCard(
                day = "15",
                doctorName = "Dr. Emily Watson",
                appointmentType = "General Check-up",
                time = "10:00AM"
            )
        }

        // Medication Reminders section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Medication Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            MedicationCard(
                name = "Amoxicillin",
                dosage = "500mg 3x daily",
                time = "After meals"
            )

            Spacer(modifier = Modifier.height(8.dp))

            MedicationCard(
                name = "Vitamin D",
                dosage = "2000IU 1x daily",
                time = "Morning"
            )
        }

        // Quick Actions section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionButton(
                    text = "Set reminder",
                    iconRes = R.drawable.reminder // Replace with your icon
                )

                QuickActionButton(
                    text = "History",
                    iconRes = R.drawable.calendar // Replace with your icon
                )

                QuickActionButton(
                    text = "AI Health",
                    iconRes = R.drawable.code // Replace with your icon
                )

                QuickActionButton(
                    text = "Support",
                    iconRes = R.drawable.chat // Replace with your icon
                )
            }
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                fontWeight = FontWeight.Bold
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    fontWeight = FontWeight.Bold
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
                color = Color(0xFF673AB7)
            )
        }
    }
}

@Composable
fun MedicationCard(
    name: String,
    dosage: String,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    painter = painterResource(id = R.drawable.fire), // Replace with your icon
                    contentDescription = "Medication",
                    tint = Color(0xFF673AB7)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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
                    color = Color(0xFF673AB7)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(text: String, iconRes: Int) {
    Column(
        modifier = Modifier
            .width(80.dp),
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
            color = Color.Black
        )
    }
}

data class HealthSummaryItem(
    val title: String,
    val value: String
)

val healthSummaryItems = listOf(
    HealthSummaryItem("Blood Pressure", "120/80"),
    HealthSummaryItem("Heart Rate", "72bpm"),
    HealthSummaryItem("Weight", "65kg")
)