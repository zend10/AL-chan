package com.zen.alchan.ui.user

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.activity
import al_chan.composeapp.generated.resources.ic_social
import al_chan.composeapp.generated.resources.reviews
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DefaultCard
import com.zen.alchan.ui.component.DefaultImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserQuickMenu() {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = DefaultTheme.dimen.paddingNormal)
            .padding(top = DefaultTheme.dimen.paddingVeryBig),
        horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)
    ) {
//        UserQuckMenuItem(
//            Res.string.anime,
//            value = "1000",
//            onClick = { }
//        )
//        UserQuckMenuItem(
//            Res.string.manga,
//            value = "1000",
//            onClick = { }
//        )
//        UserQuckMenuItem(
//            Res.string.following,
//            value = "23",
//            onClick = { }
//        )
//        UserQuckMenuItem(
//            Res.string.followers,
//            value = "12",
//            onClick = { }
//        )
        UserQuckMenuItem(
            Res.string.activity,
            Res.drawable.ic_social,
            onClick = { }
        )
        UserQuckMenuItem(
            Res.string.reviews,
            Res.drawable.ic_social,
            onClick = { }
        )
    }
}

@Composable
private fun UserQuckMenuItem(
    label: StringResource,
    icon: DrawableResource? = null,
    value: String? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultCard(
            shape = CircleShape,
            modifier = Modifier.size(56.dp).clip(CircleShape).clickable(onClick = onClick)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (icon != null) {
                    DefaultImage(
                        drawableResource = icon,
                        contentDescription = stringResource(label),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                } else if (value != null) {
                    ClickableText(
                        text = value,
                        textStyle = MaterialTheme.typography.titleMedium,
                        onClick = { },
                        modifier = Modifier
                            .clickable(enabled = false, onClick = {})
                    )
                }
            }
        }
        ClickableText(
            text = stringResource(label),
            textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            onClick = { },
            modifier = Modifier
                .padding(top = DefaultTheme.dimen.paddingVerySmall)
                .clickable(enabled = false, onClick = {})
        )
    }
}

@Composable
@Preview
fun PreviewScreen_HomeQuickMenu() {
    PreviewScreen { UserQuickMenu() }
}