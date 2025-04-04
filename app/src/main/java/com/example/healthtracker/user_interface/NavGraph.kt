package com.example.healthtracker.user_interface

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.healthtracker.user_interface.screens.AIScreen
import com.example.healthtracker.user_interface.screens.HelpCenterScreen
import com.example.healthtracker.user_interface.screens.HistoryScreen
import com.example.healthtracker.user_interface.screens.LoginScreen
import com.example.healthtracker.user_interface.screens.SignUpScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "ai") {
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("helpcenter") { HelpCenterScreen(navController) }
        composable("ai") { AIScreen(navController) }

    }
}
