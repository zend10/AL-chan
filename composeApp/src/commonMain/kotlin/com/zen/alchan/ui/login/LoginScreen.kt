package com.zen.alchan.ui.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Login

fun NavGraphBuilder.loginDestination() {
    composable<Login> { LoginScreen() }
}

fun NavController.navigateToLogin() {
    navigate(Login)
}

@Composable
fun LoginScreen() {
    Text("Login")
}