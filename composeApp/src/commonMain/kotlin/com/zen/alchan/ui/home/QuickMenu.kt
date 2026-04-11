package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.calendar
import al_chan.composeapp.generated.resources.explore
import al_chan.composeapp.generated.resources.ic_calendar
import al_chan.composeapp.generated.resources.ic_explore
import al_chan.composeapp.generated.resources.ic_seasonal
import al_chan.composeapp.generated.resources.ic_social
import al_chan.composeapp.generated.resources.seasonal
import al_chan.composeapp.generated.resources.social
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DefaultCard
import com.zen.alchan.ui.component.DefaultImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuickMenu(
    onSeasonalPressed: () -> Unit,
    onExplorePressed: () -> Unit,
    onCalendarPressed: () -> Unit,
    onSocialPressed: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = DefaultTheme.dimen.paddingNormal)
            .padding(top = DefaultTheme.dimen.paddingVeryBig),
        horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)
    ) {
        QuicKMenuItem(
            Res.string.seasonal,
            Res.drawable.ic_seasonal,
            onClick = { onSeasonalPressed() }
        )
        QuicKMenuItem(
            Res.string.explore,
            Res.drawable.ic_explore,
            onClick = { onExplorePressed() }
        )
        QuicKMenuItem(
            Res.string.calendar,
            Res.drawable.ic_calendar,
            onClick = { onCalendarPressed() }
        )
        QuicKMenuItem(
            Res.string.social,
            Res.drawable.ic_social,
            onClick = { onSocialPressed() }
        )
    }
}

@Composable
private fun QuicKMenuItem(
    label: StringResource,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultCard(
            shape = CircleShape,
            modifier = Modifier.clip(CircleShape).clickable(onClick = onClick)
        ) {
            DefaultImage(
                drawableResource = icon,
                contentDescription = stringResource(label),
                modifier = Modifier.padding(DefaultTheme.dimen.paddingNormal),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        ClickableText(
            text = stringResource(label),
            textStyle = MaterialTheme.typography.bodySmall,
            onClick = { },
            modifier = Modifier
                .padding(top = DefaultTheme.dimen.paddingVerySmall)
                .clickable(enabled = false, onClick = {})
        )
    }
}

@Composable
@Preview
fun PreviewScreen_QuickMenu() {
    PreviewScreen { QuickMenu({}, {}, {}, {}) }
}