package com.example.healthtracker.user_interface

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
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth: FirebaseAuth = Firebase.auth

    // Track authentication and loading states
    var isAuthenticated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Check auth state when the app starts
    LaunchedEffect(Unit) {
        try {
            // Refresh the auth state to ensure it's current
            auth.currentUser?.reload()?.await()
            isAuthenticated = auth.currentUser != null
        } catch (e: Exception) {
            // If there's an error, assume not authenticated
            isAuthenticated = false
        } finally {
            isLoading = false
        }
    }

    // Set the start destination based on auth state
    val startDestination = when {
        isLoading -> "loading"  // Show loading screen while checking auth
        isAuthenticated -> "home"
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
        composable("loading") { SplashScreen(navController)} // Simple loading indicator
    }
}