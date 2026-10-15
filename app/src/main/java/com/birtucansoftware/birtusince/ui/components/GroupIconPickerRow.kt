package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun GroupIconPickerRow(
    selectedIconName: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PhosphorIcons.GroupIcons.forEach { (name, iconData) ->
            val isChosen = selectedIconName.equals(name, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isChosen) colors.primarySoft else colors.surfaceHigh)
                    .border(
                        1.dp,
                        if (isChosen) colors.primaryStrong else colors.outline,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(name) },
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(
                    icon = iconData,
                    contentDescription = name,
                    tint = if (isChosen) colors.primaryStrong else colors.text,
                    size = 24.dp
                )
            }
        }
    }
}
