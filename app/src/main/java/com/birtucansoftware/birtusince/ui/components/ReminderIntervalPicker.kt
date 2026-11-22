package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

private const val MIN_REMINDER_VALUE = 1
private const val MAX_REMINDER_VALUE = 999
private const val MAX_REMINDER_DIGITS = 3

@Composable
fun ReminderIntervalPicker(
    value: Int,
    unitIndex: Int,
    onValueChange: (Int) -> Unit,
    onUnitIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val focusManager = LocalFocusManager.current

    // Local text so the field can be temporarily empty while the user types.
    var text by remember { mutableStateOf(value.toString()) }
    // Keep the field in sync when the value changes from the steppers or the parent.
    LaunchedEffect(value) {
        if (text.toIntOrNull() != value) text = value.toString()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Stepper: minus, editable number, plus (48dp touch targets)
        Row(verticalAlignment = Alignment.CenterVertically) {
            BirtuIconButton(
                icon = PhosphorIcons.Minus,
                contentDescription = "Decrease",
                onClick = {
                    if (value > MIN_REMINDER_VALUE) {
                        onValueChange(value - 1)
                    }
                },
                size = 48.dp,
                iconSize = 20.dp,
                backgroundColor = colors.surfaceHigh
            )
            BasicTextField(
                value = text,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(MAX_REMINDER_DIGITS)
                    text = digits
                    val parsed = digits.toIntOrNull()
                    if (parsed != null && parsed >= MIN_REMINDER_VALUE) {
                        onValueChange(parsed)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(56.dp)
                    .semantics { contentDescription = "Reminder amount" }
                    .onFocusChanged { state ->
                        // Restore the last valid value if the field is left empty or at 0.
                        if (!state.isFocused) {
                            val parsed = text.toIntOrNull()
                            if (parsed == null || parsed < MIN_REMINDER_VALUE) {
                                text = value.toString()
                            }
                        }
                    },
                textStyle = BirtuTypography.titleL.copy(
                    color = colors.text,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
            BirtuIconButton(
                icon = PhosphorIcons.Plus,
                contentDescription = "Increase",
                onClick = {
                    if (value < MAX_REMINDER_VALUE) {
                        onValueChange(value + 1)
                    }
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
