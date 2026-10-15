package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
fun BirtuIconButton(
    icon: PhosphorIconData,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalBirtuColors.current.text,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (borderColor != Color.Transparent) Modifier.border(1.dp, borderColor, CircleShape)
                else Modifier
            )
            .birtuPress(scaleDown = 0.94f, shape = CircleShape, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        PhosphorIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
            size = iconSize
        )
    }
}
