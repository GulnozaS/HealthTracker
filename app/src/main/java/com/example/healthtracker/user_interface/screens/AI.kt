package com.example.healthtracker.user_interface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
            .background(Color(0xFFF0F0F0))
            .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 40.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column {
            Text(
                text = "AIHealth Assistant",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your personal health companion",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Divider(
            color = Color.LightGray.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        // Input Section
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "How are you feeling today?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Analyze Symptoms", fontSize = 16.sp)
            }
        }

        Divider(
            color = Color.LightGray.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        // Health Insights Section
        Text(
            text = "Health Insights",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        HealthInsightCard(
            title = "Risk Assessment",
            content = "Based on your recent blood pressure readings, consider reducing sodium intake and increasing physical activity."
        )

        HealthInsightCard(
            title = "Lifestyle Recommendation",
            content = "Your sedentary lifestyle pattern suggests adding 30 minutes of daily moderate exercise."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Text(
            text = "Disclaimer: AI suggestions do not replace professional medical advice.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun HealthInsightCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF673AB7),
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
