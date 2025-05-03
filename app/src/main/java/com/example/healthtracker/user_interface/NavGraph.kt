package com.example.healthtracker.user_interface

import AIScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.user_interface.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth: FirebaseAuth = Firebase.auth
    val firestore = Firebase.firestore

    // Track authentication, profile completion, and loading states
    var isAuthenticated by remember { mutableStateOf(false) }
    var isProfileComplete by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Check auth state and profile completion when the app starts
    LaunchedEffect(Unit) {
        try {
            // Refresh auth state
            auth.currentUser?.reload()?.await()
            val authenticated = auth.currentUser != null
            isAuthenticated = authenticated

            // If authenticated, check profile completion
            if (authenticated) {
                val userId = auth.currentUser?.uid ?: return@LaunchedEffect
                val userDoc = firestore.collection("users").document(userId).get().await()
                isProfileComplete = userDoc.exists() &&
                        (userDoc.get("personalInfo") as? Map<String, Any>)?.get("gender")?.toString()?.isNotEmpty() == true            }
        } catch (e: Exception) {
            // On error, treat as unauthenticated
            isAuthenticated = false
            isProfileComplete = false
        } finally {
            isLoading = false
        }
    }

    // Set the start destination based on auth and profile state
    val startDestination = when {
        isLoading -> "loading"
        isAuthenticated && isProfileComplete -> "home"
        isAuthenticated && !isProfileComplete -> "account_setup"
        else -> "signup"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("history") { HistoryScreen(navController) }
        composable("helpcenter") { HelpCenterScreen(navController) }
        composable("ai") { AIScreen(navController) }
        composable("account_setup") { AccountSetupScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("loading") { SplashScreen() }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
    }
}

