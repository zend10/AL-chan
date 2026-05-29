package com.zen.alchan.ui.social

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
object Social

fun NavGraphBuilder.socialDestination() {
    composable<Social> { SocialScreen() }
}

fun NavController.navigateToSocial() {
    navigate(Social)
}

@Composable
fun SocialScreen() {
    Scaffold {
        DisplayText("Social", MaterialTheme.typography.titleLarge)
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_SocialScreen() {
    PreviewScreen {
        SocialScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_SocialScreen() {
    PreviewScreen {
        SocialScreen()
    }
}