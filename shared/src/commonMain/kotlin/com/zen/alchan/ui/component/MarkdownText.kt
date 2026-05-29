package com.zen.alchan.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun MarkdownText(
    richTextState: RichTextState,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    richTextState.config.linkColor = MaterialTheme.colorScheme.primary
    richTextState.config.linkTextDecoration = TextDecoration.None

    RichText(
        richTextState,
        modifier = modifier,
        style = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
    )
}

@Composable
@Preview
fun PreviewScreen_MarkdownText() {
    PreviewScreen {
        MarkdownText(
            richTextState = rememberRichTextState().setMarkdown(
                """
                    Hello, World!
                    *Hello, World!*
                    **Hello, World!**
                    [Hello, World!](https://anilist.co)
                    ~~Hello, World!~~
                    `Hello, World!`
                    - Hello, World!
                    - Hello, World!
                    1. Hello, World!
                    2. Hello, World!
                """.trimIndent()
            ),
            textStyle = MaterialTheme.typography.bodySmall
        )
    }
}