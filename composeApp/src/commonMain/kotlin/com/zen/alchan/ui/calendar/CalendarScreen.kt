package com.zen.alchan.ui.calendar

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
object Calendar

fun NavGraphBuilder.calendarDestination() {
    composable<Calendar> { CalendarScreen() }
}

fun NavController.navigateToCalendar() {
    navigate(Calendar)
}

@Composable
fun CalendarScreen() {
    Scaffold {
        DisplayText("Calendar", MaterialTheme.typography.titleLarge)
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_CalendarScreen() {
    PreviewScreen {
        CalendarScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_CalendarScreen() {
    PreviewScreen {
        CalendarScreen()
    }
}