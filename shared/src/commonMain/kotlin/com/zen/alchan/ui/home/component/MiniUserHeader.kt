@file:OptIn(ExperimentalMaterial3Api::class)

package com.zen.alchan.ui.home.component

import al_chan.shared.generated.resources.Res
import al_chan.shared.generated.resources.ic_arrow_forward
import al_chan.shared.generated.resources.ic_notifications
import al_chan.shared.generated.resources.ic_settings
import al_chan.shared.generated.resources.notifications
import al_chan.shared.generated.resources.settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.api.User
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.AvatarImage
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.CollapsingTopBar
import com.zen.alchan.ui.component.TopBarButton

@Composable
fun MiniUserHeader(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    user: User,
    appConfig: AppConfig,
    onUserClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CollapsingTopBar(
        topAppBarScrollBehavior,
        backgroundImageUrl = user.bannerImage,
        aspectRatio = 2.5f,
        alwaysDisplayedHeight = DefaultTheme.dimen.iconBig + DefaultTheme.dimen.paddingNormal + DefaultTheme.dimen.paddingNormal,
        alwaysDisplayedContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = DefaultTheme.dimen.paddingNormal)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarImage(
                        user.getAvatar(appConfig),
                        size = DefaultTheme.dimen.iconBig,
                        modifier = Modifier.padding(end = DefaultTheme.dimen.paddingVerySmall),
                        onClick = onUserClick
                    )
                    ClickableText(
                        user.name,
                        textStyle = MaterialTheme.typography.titleLarge,
                        onClick = onUserClick,
                        trailingIcon = Res.drawable.ic_arrow_forward
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)
                ) {
                    TopBarButton(
                        Res.drawable.ic_notifications,
                        Res.string.notifications,
                        onNotificationsClick
                    )
                    TopBarButton(
                        Res.drawable.ic_settings,
                        Res.string.settings,
                        onSettingsClick
                    )
                }
            }
        }
    )
}

@Composable
@Preview
fun PreviewScreen_Mini_UserHeader() {
    val user = User(id = "123", name = "Bob")
    PreviewScreen {
        MiniUserHeader(
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            user,
            AppConfig(),
            {},
            {},
            {}
        )
    }
}