package com.birtucansoftware.birtusince.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.components.BirtuIconTile
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuSwitch
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIconData
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun SettingsSectionHeader(
    title: String,
    infoExplanation: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = BirtuTypography.label.copy(color = colors.text2, fontWeight = FontWeight.SemiBold)
        )
        if (infoExplanation != null) {
            Spacer(modifier = Modifier.width(4.dp))
            BirtuInfoIconButton(
                title = title,
                explanation = infoExplanation
            )
        }
    }
}

@Composable
fun SettingsGroupCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalBirtuColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        content = content
    )
}

@Composable
fun SettingsDivider(
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.outline)
    )
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    icon: PhosphorIconData? = null,
    showCaret: Boolean = true,
    trailingText: String? = null,
    trailingTextColor: androidx.compose.ui.graphics.Color? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (subtitle != null) 64.dp else 56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                BirtuIconTile(
                    icon = icon,
                    size = 40.dp,
                    iconSize = 20.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    style = BirtuTypography.body.copy(color = colors.text, fontWeight = FontWeight.Medium)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = BirtuTypography.caption.copy(color = colors.text2)
                    )
                }
            }
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = BirtuTypography.body.copy(color = trailingTextColor ?: colors.text2)
            )
        }

        if (showCaret) {
            PhosphorIcon(
                icon = PhosphorIcons.CaretRight,
                contentDescription = null,
                tint = colors.text2,
                size = 18.dp
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    icon: PhosphorIconData? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                BirtuIconTile(
                    icon = icon,
                    size = 40.dp,
                    iconSize = 20.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(text = title, style = BirtuTypography.body.copy(color = colors.text))
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = BirtuTypography.caption.copy(color = colors.text2)
                    )
                }
            }
        }

        BirtuSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
