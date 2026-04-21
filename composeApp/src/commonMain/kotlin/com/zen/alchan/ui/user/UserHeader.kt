@file:OptIn(ExperimentalMaterial3Api::class)

package com.zen.alchan.ui.user

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.anime
import al_chan.composeapp.generated.resources.back
import al_chan.composeapp.generated.resources.followers
import al_chan.composeapp.generated.resources.following
import al_chan.composeapp.generated.resources.ic_back
import al_chan.composeapp.generated.resources.manga
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.api.User
import com.zen.alchan.helper.applyTopBarMinHeight
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.AvatarImage
import com.zen.alchan.ui.component.CollapsingTopBar
import com.zen.alchan.ui.component.DefaultCard
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.PrimaryButton
import com.zen.alchan.ui.component.TopBarButton
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserHeader(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    user: User,
    appConfig: AppConfig,
    onBackClick: () -> Unit
) {
    CollapsingTopBar(
        topAppBarScrollBehavior,
        backgroundImageUrl = user.bannerImage,
        anchorContent = {
            AnchorContent()
        },
        anchorContentHeight = 60.dp,
        fullyExpandedContent = {
            HeaderContent(user, appConfig)
        },
        alwaysDisplayedContent = {
            Row(
                modifier = Modifier.fillMaxWidth().applyTopBarMinHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarButton(
                    Res.drawable.ic_back,
                    Res.string.back,
                    onBackClick
                )
                Spacer(Modifier.weight(1f))
            }
        }
    )
}

@Composable
private fun HeaderContent(user: User, appConfig: AppConfig) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = DefaultTheme.dimen.paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        AvatarImage(user.getAvatar(appConfig))
        DisplayText(
            user.name,
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = DefaultTheme.dimen.paddingSmall)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)) {
            Card(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
            ) {
                Text(
                    "Donator",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.background),
                    modifier = Modifier.padding(horizontal = DefaultTheme.dimen.paddingVerySmall)
                )
            }
            Card(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
            ) {
                Text(
                    "Mod",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.background),
                    modifier = Modifier.padding(horizontal = DefaultTheme.dimen.paddingVerySmall)
                )
            }
        }
        PrimaryButton(
            "Follow",
            onClick = {},
            modifier = Modifier.padding(top = DefaultTheme.dimen.paddingSmall)
        )
    }
}

@Composable
private fun AnchorContent() {
    DefaultCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnchorContentItem(
                "100",
                Res.string.anime,
                Modifier.weight(1f),
                onClick = {}
            )
            AnchorContentItem(
                "200",
                Res.string.manga,
                Modifier.weight(1f),
                onClick = {}
            )
            AnchorContentItem(
                "12",
                Res.string.following,
                Modifier.weight(1f),
                onClick = {}
            )
            AnchorContentItem(
                "3",
                Res.string.followers,
                Modifier.weight(1f),
                onClick = {}
            )
        }
    }
}

@Composable
private fun AnchorContentItem(
    value: String,
    label: StringResource,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.height(60.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            stringResource(label),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
@Preview
fun PreviewScreen_UserHeader() {
    val user = User(name = "Bob")
    PreviewScreen {
        UserHeader(TopAppBarDefaults.exitUntilCollapsedScrollBehavior(), user, AppConfig(), {})
    }
}