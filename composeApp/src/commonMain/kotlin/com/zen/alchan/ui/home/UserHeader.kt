@file:OptIn(ExperimentalMaterial3Api::class)

package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.ic_arrow_forward
import al_chan.composeapp.generated.resources.ic_notifications
import al_chan.composeapp.generated.resources.notifications
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.api.User
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.CollapsingTopBar
import com.zen.alchan.ui.component.DefaultImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserHeader(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    user: User,
    appConfig: AppConfig,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    CollapsingTopBar(
        topAppBarScrollBehavior,
        backgroundImageUrl = user.bannerImage,
        fullyExpandedContent = {

        },
        alwaysDisplayedContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DefaultTheme.dimen.paddingNormal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultImage(
                    imageUrl = user.getAvatar(appConfig),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = DefaultTheme.dimen.paddingVerySmall)
                        .clip(CircleShape)
                        .size(DefaultTheme.dimen.iconBig)
                        .clickable(onClick = onProfileClick)
                )
                ClickableText(
                    user.name,
                    textStyle = MaterialTheme.typography.titleLarge,
                    onClick = onProfileClick
                )
                DefaultImage(
                    drawableResource = Res.drawable.ic_arrow_forward,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(start = DefaultTheme.dimen.paddingVerySmall)
                        .size(DefaultTheme.dimen.iconSmall)
                        .clickable(onClick = onProfileClick)
                )
                Spacer(Modifier.weight(1f))
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim)
                ) {
                    DefaultImage(
                        drawableResource = Res.drawable.ic_notifications,
                        contentDescription = stringResource(Res.string.notifications),
                        modifier = Modifier
                            .padding(DefaultTheme.dimen.paddingVerySmall)
                            .size(DefaultTheme.dimen.iconNormal)
                            .clickable(onClick = onNotificationsClick),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    )
}

@Composable
@Preview
fun PreviewScreen_UserHeader() {
    val user = User(id = "123", name = "Bob")
    PreviewScreen {
        UserHeader(
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            user,
            AppConfig(),
            {},
            {}
        )
    }
}