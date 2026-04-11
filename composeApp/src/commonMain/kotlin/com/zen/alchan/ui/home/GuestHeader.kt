package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.guest_greetings_body
import al_chan.composeapp.generated.resources.guest_greetings_title
import al_chan.composeapp.generated.resources.guest_wallpaper
import al_chan.composeapp.generated.resources.log_in
import al_chan.composeapp.generated.resources.login_body
import al_chan.composeapp.generated.resources.login_title
import al_chan.composeapp.generated.resources.register
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.zen.alchan.DefaultTheme
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DefaultImage
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.MarkdownText
import com.zen.alchan.ui.component.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestHeader(onClickRegister: () -> Unit, onClickLogin: () -> Unit) {
    val guestLoginRichTextState = rememberRichTextState()
    val guestText = stringResource(Res.string.login_body)

    LaunchedEffect(Unit) {
        guestLoginRichTextState.setMarkdown(guestText)
    }

    Box {
        DefaultImage(
            drawableResource = Res.drawable.guest_wallpaper,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).applyGradientOverlay(0f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(DefaultTheme.dimen.paddingNormal)
                .align(Alignment.BottomStart)
        ) {
            DisplayText(
                text = stringResource(Res.string.guest_greetings_title),
                textStyle = MaterialTheme.typography.titleMedium,
            )
            DisplayText(
                text = stringResource(Res.string.guest_greetings_body),
                textStyle = MaterialTheme.typography.bodySmall,
            )
            DisplayText(
                text = stringResource(Res.string.login_title),
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = DefaultTheme.dimen.paddingNormal)
            )
            MarkdownText(
                richTextState = guestLoginRichTextState,
                textStyle = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = DefaultTheme.dimen.paddingNormal),
                horizontalArrangement = Arrangement.spacedBy(
                    DefaultTheme.dimen.paddingNormal,
                    Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableText(
                    text = stringResource(Res.string.register),
                    onClick = { onClickRegister() },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                PrimaryButton(
                    text = stringResource(Res.string.log_in),
                    onClick = { onClickLogin() },
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewScreen_GuestHeader() {
    PreviewScreen { GuestHeader({}, {}) }
}