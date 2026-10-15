package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun ReminderIntervalPicker(
    value: Int,
    unitIndex: Int,
    onValueChange: (Int) -> Unit,
    onUnitIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Stepper: minus, number, plus (48dp touch targets)
        Row(verticalAlignment = Alignment.CenterVertically) {
            BirtuIconButton(
                icon = PhosphorIcons.fromName("minus"),
                contentDescription = "Decrease",
                onClick = {
                    if (value > 1) {
                        onValueChange(value - 1)
                    }
                },
                size = 48.dp,
                iconSize = 20.dp,
                backgroundColor = colors.surfaceHigh
            )
            Text(
                text = "$value",
                style = BirtuTypography.titleL.copy(
                    color = colors.text,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            BirtuIconButton(
                icon = PhosphorIcons.Plus,
                contentDescription = "Increase",
                onClick = {
                    onValueChange(value + 1)
                },
                size = 48.dp,
                iconSize = 20.dp,
                backgroundColor = colors.surfaceHigh
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Three-way segment: Min | Hours | Days
        BirtuSegmentedControl(
            items = listOf("Min", "Hours", "Days"),
            selectedIndex = unitIndex,
            onItemSelected = onUnitIndexChange,
            modifier = Modifier.weight(1f)
        )
    }
}
