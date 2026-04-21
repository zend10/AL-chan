package com.zen.alchan.ui.user

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.bio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.MarkdownText
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserBio(bio: String) {
    val bioRichTextState = rememberRichTextState()

    LaunchedEffect(bio) {
        bioRichTextState.setMarkdown(bio)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        DisplayText(
            text = stringResource(Res.string.bio),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                top = DefaultTheme.dimen.paddingVeryBig,
                bottom = DefaultTheme.dimen.paddingNormal,
                start = DefaultTheme.dimen.paddingNormal,
                end = DefaultTheme.dimen.paddingNormal
            ),
        )
        MarkdownText(
            richTextState = bioRichTextState,
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = DefaultTheme.dimen.paddingNormal)
        )
    }
}

@Composable
@Preview
fun PreviewScreen_UserBio() {
    PreviewScreen { UserBio("Hello, World!") }
}