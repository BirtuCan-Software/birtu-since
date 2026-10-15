package com.birtucansoftware.birtusince.ui.detail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuSheetContainer
import com.birtucansoftware.birtusince.ui.components.BirtuTonalButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import java.util.Calendar

@Composable
fun StartOverSheet(
    visible: Boolean,
    runDurationCompact: String,
    onDismissRequest: () -> Unit,
    onStartOverNow: () -> Unit,
    onStartOverPast: (epochMillis: Long) -> Unit,
    minAllowedStartMillis: Long = 0L
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    BirtuSheetContainer(
        visible = visible,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Start over?",
                style = BirtuTypography.titleL.copy(color = colors.text)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "This run ($runDurationCompact) will be saved.",
                style = BirtuTypography.body.copy(color = colors.text2)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary: Just now (56 dp)
            BirtuPrimaryButton(
                text = "Just now",
                icon = PhosphorIcons.ArrowCounterClockwise,
                onClick = onStartOverNow,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick picks row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BirtuTonalButton(
                    text = "1 hour ago",
                    onClick = {
                        val pastTime = System.currentTimeMillis() - (60 * 60 * 1000)
                        if (pastTime >= minAllowedStartMillis) {
                            onStartOverPast(pastTime)
                        } else {
                            onStartOverNow()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                BirtuTonalButton(
                    text = "Yesterday",
                    onClick = {
                        val pastTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                        if (pastTime >= minAllowedStartMillis) {
                            onStartOverPast(pastTime)
                        } else {
                            onStartOverNow()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pick a time...
            BirtuTonalButton(
                text = "Pick a time...",
                icon = PhosphorIcons.CalendarBlank,
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            cal.set(Calendar.YEAR, y)
                            cal.set(Calendar.MONTH, m)
                            cal.set(Calendar.DAY_OF_MONTH, d)
                            TimePickerDialog(
                                context,
                                { _, h, min ->
                                    cal.set(Calendar.HOUR_OF_DAY, h)
                                    cal.set(Calendar.MINUTE, min)
                                    val chosen = cal.timeInMillis
                                    if (chosen in minAllowedStartMillis..System.currentTimeMillis()) {
                                        onStartOverPast(chosen)
                                    }
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        datePicker.maxDate = System.currentTimeMillis()
                        if (minAllowedStartMillis > 0) {
                            datePicker.minDate = minAllowedStartMillis
                        }
                    }.show()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
