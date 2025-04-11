package com.example.healthtracker.user_interface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))  // Same light gray bg
            .padding(16.dp)  // Consistent padding
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)  // Uniform spacing
    ) {
        // Header (matches LoginScreen)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Help Center",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Get assistance for all your health needs",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Search Bar (identical to LoginScreen fields)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search help articles...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray  // Same gray icon
                )
            },
            shape = RoundedCornerShape(12.dp),  // Matched rounded corners
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF673AB7)  // Purple focus
            )
        )

        // Quick Actions (styled like LoginScreen buttons)
        Text(
            text = "Quick Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton("Live Chat")  // Reusable component
            ActionButton("Call Us")
            ActionButton("Email")
        }

        // FAQ Section (white card like HistoryScreen)
        Text(
            text = "FAQs",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)  // Same as HistoryScreen
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                FAQItem("How to update health profile?")
                FAQItem("Schedule a consultation?")
                FAQItem("View test results?")
            }
        }
    }
}

// Reusable Purple-Outline Button (like LoginScreen's secondary actions)
@Composable
private fun ActionButton(text: String) {
    Button(
        onClick = { /* Handle click */ },
        modifier = Modifier.width(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF673AB7)  // Purple text
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            color = Color(0xFF673AB7)  // Purple border
        ),
        shape = RoundedCornerShape(12.dp)  // Slightly rounded
    ) {
        Text(text, fontSize = 14.sp)
    }
}

// FAQ Item with Divider (clean, like HistoryScreen's list)
@Composable
private fun FAQItem(question: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Divider(
            color = Color.LightGray.copy(alpha = 0.5f),  // Subtle divider
            thickness = 1.dp
        )
    }
}