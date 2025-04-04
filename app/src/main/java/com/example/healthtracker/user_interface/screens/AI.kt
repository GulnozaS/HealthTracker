package com.example.healthtracker.user_interface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AIScreen(navController: NavController) {
    var symptoms by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AIHealth Assistant",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32) // Dark green
            )
            Text(
                text = "Your personal health companion",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Symptoms Input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "How are you feeling today?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = symptoms,
                onValueChange = { symptoms = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Describe your symptoms...") },
                shape = RoundedCornerShape(12.dp)
            )
            Button(
                onClick = { /* Analyze symptoms */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Analyze Symptoms", fontSize = 16.sp)
            }
        }

        // Health Insights Section
        Text(
            text = "Health Insights",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Risk Assessment Card
        HealthInsightCard(
            title = "Risk Assessment",
            content = "Based on your recent blood pressure readings, consider reducing sodium intake and increasing physical activity."
        )

        // Lifestyle Recommendation Card
        HealthInsightCard(
            title = "Lifestyle Recommendation",
            content = "Your sedentary lifestyle pattern suggests adding 30 minutes of daily moderate exercise."
        )
    }
}

@Composable
private fun HealthInsightCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Light green
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}