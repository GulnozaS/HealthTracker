package com.example.healthtracker.user_interface.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpCenterScreen(navController: NavController) {
    val context = LocalContext.current
    val phoneNumber = "+998990000000"
    val emailAddress = "akhmadjanovagulrukhsor@gmail.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = "Help Center",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Get assistance and support for all your health needs",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Divider(
            color = Color.LightGray.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Quick Actions
        Text(
            text = "Quick Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton("Call Us", Modifier.width(110.dp)) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                context.startActivity(intent)
            }
            ActionButton("Email", Modifier.width(110.dp)) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$emailAddress")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                    putExtra(Intent.EXTRA_SUBJECT, "Health Tracker Support")
                }
                context.startActivity(intent)
            }
        }

        // FAQ Section
        Text(
            text = "Frequently Asked Questions",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FAQSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Footer
        Text(
            text = "We're here to help 24/7. Average response time: 5 minutes",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF673AB7),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// FAQ Data Class with state
data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

@Composable
fun FAQSection() {
    val faqItems = remember {
        mutableStateListOf(
            FAQItem("How do I update my health profile?", "Go to Profile > Edit Profile to update your information."),
            FAQItem("How to schedule a consultation?", "Visit Appointments to book with your preferred provider."),
            FAQItem("Where can I find my medical records?", "All records are organized in the History section.")
        )
    }

    Column {
        faqItems.forEachIndexed { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        faqItems[index] = item.copy(isExpanded = !item.isExpanded)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (item.isExpanded) Color(0xFF673AB7) else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }

                    if (item.isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.answer,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}