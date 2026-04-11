package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.guest_greetings_body
import al_chan.composeapp.generated.resources.guest_greetings_title
import al_chan.composeapp.generated.resources.log_in
import al_chan.composeapp.generated.resources.login_body
import al_chan.composeapp.generated.resources.login_title
import al_chan.composeapp.generated.resources.register
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.MarkdownText
import com.zen.alchan.ui.component.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestHeader() {
    val guestLoginRichTextState = rememberRichTextState()
    val guestText = stringResource(Res.string.login_body)

    LaunchedEffect(Unit) {
        guestLoginRichTextState.setMarkdown(guestText)
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(DefaultTheme.dimen.paddingNormal)
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