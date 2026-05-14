@file:OptIn(ExperimentalMaterial3Api::class)

package com.zen.alchan.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.user.component.BioSection
import com.zen.alchan.ui.user.component.UserHeader
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class User(val id: String)

fun NavGraphBuilder.userDestination(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    composable<User> {
        UserScreen(
            UserParam(it.toRoute<User>().id),
            onBackClick,
            onCloseClick,
        )
    }
}

fun NavController.navigateToUser(id: String, isStartDestination: Boolean) {
    navigate(User(id)) {
        if (isStartDestination) {
            popUpTo(graph.startDestinationId) {
                inclusive = true
            }
        }
    }
}

@Composable
fun UserScreen(
    userParam: UserParam,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    val viewModel = koinViewModel<UserViewModel> { parametersOf(userParam) }
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            UserHeader(
                topAppBarScrollBehavior,
                state.user,
                state.appConfig,
                onBackClick = { onBackClick() },
                onCloseClick = { onCloseClick() },
                onFollowClick = { }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
                .padding(bottom = DefaultTheme.dimen.paddingVeryBig)
        ) {
            BioSection(state.user.about)
        }
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_UserScreen() {
    PreviewScreen {
        UserScreen(UserParam(), {}, {})
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_UserScreen() {
    PreviewScreen {
        UserScreen(UserParam(), {}, {})
    }
}