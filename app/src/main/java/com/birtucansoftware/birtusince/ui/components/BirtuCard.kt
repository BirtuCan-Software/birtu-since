package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIconData
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun BirtuCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    backgroundColor: Color = LocalBirtuColors.current.surface,
    borderColor: Color = LocalBirtuColors.current.outline,
    cornerRadius: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .birtuCardPress(shape = shape, onLongClick = onLongClick, onClick = onClick)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun BirtuIconTile(
    icon: PhosphorIconData,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 26.dp,
    backgroundColor: Color = LocalBirtuColors.current.primarySoft,
    tint: Color = LocalBirtuColors.current.primaryStrong
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        PhosphorIcon(
            icon = icon,
            contentDescription = null,
            tint = tint,
            size = iconSize
        )
    }
}
