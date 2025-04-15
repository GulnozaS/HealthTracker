package com.example.healthtracker.user_interface

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.healthtracker.user_interface.screens.AccountSetupScreen
import com.example.healthtracker.user_interface.screens.HomeScreen
import com.example.healthtracker.user_interface.screens.LoginScreen
import com.example.healthtracker.user_interface.screens.SignUpScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("account_setup") { AccountSetupScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}
