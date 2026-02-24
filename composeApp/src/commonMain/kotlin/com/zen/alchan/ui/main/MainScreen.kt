package com.zen.alchan.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Main

fun NavGraphBuilder.mainDestination() {
    composable<Main> { MainScreen() }
}

fun NavController.navigateToMain() {
    navigate(Main)
}

@Composable
fun MainScreen() {
    Text("Main")
}