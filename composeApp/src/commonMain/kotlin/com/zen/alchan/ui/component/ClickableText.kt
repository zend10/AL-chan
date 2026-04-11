package com.zen.alchan.ui.component

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.ic_arrow_forward
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ClickableText(
    text: String,
    textStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: DrawableResource? = null
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            modifier = modifier,
            style = textStyle.copy(color = MaterialTheme.colorScheme.primary)
        )
        if (trailingIcon != null) {
            DefaultImage(
                drawableResource = trailingIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(DefaultTheme.dimen.iconSmall)
            )
        }
    }

}

@Composable
@Preview
fun PreviewScreen_ClickableText() {
    PreviewScreen {
        ClickableText(
            text = "Hello, World!",
            textStyle = MaterialTheme.typography.bodyLarge,
            onClick = { },
            trailingIcon = Res.drawable.ic_arrow_forward
        )
    }
}