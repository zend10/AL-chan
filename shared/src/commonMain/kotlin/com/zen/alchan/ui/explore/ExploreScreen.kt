package com.zen.alchan.ui.explore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.DisplayText
import kotlinx.serialization.Serializable

@Serializable
object Explore

fun NavGraphBuilder.exploreDestination() {
    composable<Explore> { ExploreScreen() }
}

fun NavController.navigateToExplore() {
    navigate(Explore)
}

@Composable
fun ExploreScreen() {
    Scaffold {
        DisplayText("Explore", MaterialTheme.typography.titleLarge)
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_ExploreScreen() {
    PreviewScreen {
        ExploreScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_ExploreScreen() {
    PreviewScreen {
        ExploreScreen()
    }
}