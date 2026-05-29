package com.zen.alchan.ui.maindetail

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object MainDetailSplash

fun NavGraphBuilder.mainDetailSplashDestination() {
    composable<MainDetailSplash> { MainDetailSplashScreen() }
}

fun NavController.navigateToMainDetailSplash() {
    navigate(MainDetailSplash) {
        popUpTo(graph.startDestinationId) {
            inclusive = false
        }
    }
}

@Composable
fun MainDetailSplashScreen() {
}