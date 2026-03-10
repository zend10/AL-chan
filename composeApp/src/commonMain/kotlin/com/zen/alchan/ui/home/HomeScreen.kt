package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.agumon
import al_chan.composeapp.generated.resources.guest_greetings_body
import al_chan.composeapp.generated.resources.guest_greetings_title
import al_chan.composeapp.generated.resources.log_in
import al_chan.composeapp.generated.resources.login_body
import al_chan.composeapp.generated.resources.login_title
import al_chan.composeapp.generated.resources.register
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.PrimaryButton
import com.zen.alchan.ui.main.MainUiEffect
import com.zen.alchan.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

fun NavGraphBuilder.homeDestination(mainViewModel: MainViewModel) {
    composable<Home> { HomeScreen(mainViewModel) }
}

fun NavController.navigateToHome() {
    navigate(Home)
}

@Composable
fun HomeScreen(mainViewModel: MainViewModel?) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        mainViewModel?.bottomNavigationTabEffect?.collectLatest { newEffect ->
            when (newEffect) {
                MainUiEffect.ScrollHomeToTop -> scrollState.animateScrollTo(0)
                else -> { }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        GuestHeader()
        QuickMenu()
        TrendingSection(state.trending)
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxWidth()
            .safeDrawingPadding()
    ) {
        PrimaryButton(
            text = "Login",
            onClick = { },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
        DisplayText(
            "Welcome",
            textStyle = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
private fun GuestHeader() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(DefaultTheme.dimen.paddingNormal)
    ) {
        DisplayText(
            text = stringResource(Res.string.guest_greetings_title),
            textStyle = MaterialTheme.typography.titleSmall,
        )
        DisplayText(
            text = stringResource(Res.string.guest_greetings_body),
            textStyle = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(DefaultTheme.dimen.paddingNormal))
        DisplayText(
            text = stringResource(Res.string.login_title),
            textStyle = MaterialTheme.typography.titleSmall,
        )
        DisplayText(
            text = stringResource(Res.string.login_body),
            textStyle = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(DefaultTheme.dimen.paddingNormal))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                DefaultTheme.dimen.paddingNormal,
                Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClickableText(
                text = stringResource(Res.string.register),
                onClick = { },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            PrimaryButton(
                text = stringResource(Res.string.log_in),
                onClick = { },
            )
        }
    }
}

@Composable
private fun QuickMenu() {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = DefaultTheme.dimen.paddingVeryBig),
        horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)
    ) {
        QuicKMenuChip("Search", onClick = { })
        QuicKMenuChip("Seasonal", onClick = { })
        QuicKMenuChip("Explore", onClick = { })
        QuicKMenuChip("Calendar", onClick = { })
        QuicKMenuChip("Reviews", onClick = { })
        QuicKMenuChip("Timeline", onClick = { })
        QuicKMenuChip("Forum", onClick = { })
    }
}

@Composable
private fun QuicKMenuChip(
    label: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(
            label,
            modifier = Modifier.padding(8.dp)
        )
    }
//    AssistChip(
//        onClick = onClick,
//        label = {
//            Text(
//                label,
//                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
//            )
//        },
//        leadingIcon = {
//            Image(
//                painterResource(Res.drawable.agumon),
//                contentDescription = label,
//                modifier = Modifier.size(DefaultTheme.dimen.iconNormal)
//            )
//        },
//        shape = RoundedCornerShape(DefaultTheme.dimen.paddingVeryBig),
//        border = BorderStroke(DefaultTheme.dimen.lineWidth, MaterialTheme.colorScheme.primary),
//    )
}

@Composable
private fun TrendingSection(trending: List<Media>) {
    Column {
        DisplayText(
            text = "Trending Right Now",
            textStyle = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(DefaultTheme.dimen.paddingNormal)
                .padding(top = DefaultTheme.dimen.paddingVeryBig)
        )
        repeat(trending.size) { index ->
            val trendingItem = trending[index]
            TrendingItem(
                trendingItem.title.userPreferred,
                "Episode ${trendingItem.episodes ?: 0}",
                trendingItem.bannerImage
            )
        }
    }
}

@Composable
fun TrendingItem(
    title: String,
    caption: String,
    imageUrl: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            imageUrl,
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.5f)
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = size.height / 3,
                        endY = size.height,
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                }
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(DefaultTheme.dimen.paddingNormal)
        ) {
            DisplayText(title, textStyle = MaterialTheme.typography.titleLarge)
            DisplayText(caption, textStyle = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_HomeScreen() {
    PreviewScreen {
        HomeScreen(null)
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_HomeScreen() {
    PreviewScreen {
        HomeScreen(null)
    }
}