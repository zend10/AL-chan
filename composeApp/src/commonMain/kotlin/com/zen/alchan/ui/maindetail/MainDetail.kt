package com.zen.alchan.ui.maindetail

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
object MainDetail

fun NavGraphBuilder.mainDetailDestination() {
    composable<MainDetail> { MainDetailScreen() }
}

fun NavController.navigateToMainDetail() {
    navigate(MainDetail)
}

@Composable
fun MainDetailScreen() {
    Scaffold {
        DisplayText("MainDetail", MaterialTheme.typography.titleLarge)
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_MainDetailScreen() {
    PreviewScreen {
        MainDetailScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_MainDetailScreen() {
    PreviewScreen {
        MainDetailScreen()
    }
}
