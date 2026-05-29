package com.zen.alchan.ui.maindetail

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zen.alchan.data.enums.DetailPage
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.user.navigateToUser
import com.zen.alchan.ui.user.userDestination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MainDetail(val startDestination: DetailPage, val id: String)

fun NavGraphBuilder.mainDetailDestination(onCloseClick: () -> Unit) {
    composable<MainDetail> {
        val route = it.toRoute<MainDetail>()
        MainDetailScreen(MainDetailParam(route.startDestination, route.id), onCloseClick)
    }
}

fun NavController.navigateToMainDetail(startDestination: DetailPage, id: String) {
    navigate(MainDetail(startDestination, id))
}

@Composable
fun MainDetailScreen(mainDetailParam: MainDetailParam, onCloseClick: () -> Unit) {
    val viewModel = koinViewModel<MainDetailViewModel> { parametersOf(mainDetailParam) }
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                is MainDetailUiEffect.NavigateToMediaDetail -> {}
                is MainDetailUiEffect.NavigateToUser -> {
                    navController.navigateToUser(newEffect.id, true)
                }

                MainDetailUiEffect.NavigateBackOrClose -> {
                    if (navController.previousBackStackEntry == null) {
                        onCloseClick()
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {

        }
    ) {
        NavHost(
            navController = navController,
            startDestination = MainDetailSplash
        ) {
            mainDetailSplashDestination()
            userDestination(
                onBackClick = { viewModel.onBackClick() },
                onCloseClick = { onCloseClick() }
            )
        }
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_MainDetailScreen() {
    PreviewScreen {
        MainDetailScreen(MainDetailParam(DetailPage.USER, "123"), {})
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_MainDetailScreen() {
    PreviewScreen {
        MainDetailScreen(MainDetailParam(DetailPage.USER, "123"), {})
    }
}
