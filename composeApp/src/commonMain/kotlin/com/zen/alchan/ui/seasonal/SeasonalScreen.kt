package com.zen.alchan.ui.seasonal

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
object Seasonal

fun NavGraphBuilder.seasonalDestination() {
    composable<Seasonal> { SeasonalScreen() }
}

fun NavController.navigateToSeasonal() {
    navigate(Seasonal)
}

@Composable
fun SeasonalScreen() {
    Scaffold {
        DisplayText("Seasonal", MaterialTheme.typography.titleLarge)
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_SeasonalScreen() {
    PreviewScreen {
        SeasonalScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_SeasonalScreen() {
    PreviewScreen {
        SeasonalScreen()
    }
}
