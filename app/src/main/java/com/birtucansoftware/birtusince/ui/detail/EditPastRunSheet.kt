package com.birtucansoftware.birtusince.ui.detail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.data.LapEntity
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuSheetContainer
import com.birtucansoftware.birtusince.ui.components.BirtuTextButton
import com.birtucansoftware.birtusince.ui.components.BirtuTonalButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import java.util.Calendar

@Composable
fun EditPastRunSheet(
    visible: Boolean,
    lap: LapEntity,
    onDismissRequest: () -> Unit,
    onSaveLap: (updatedLap: LapEntity) -> Unit,
    onDeleteLap: (lapId: Long) -> Unit
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    var startTimestamp by remember(lap, visible) { mutableLongStateOf(lap.startEpochMillis) }
    var endTimestamp by remember(lap, visible) { mutableLongStateOf(lap.endEpochMillis) }

    val currentDuration = (endTimestamp - startTimestamp).coerceAtLeast(0L)

    BirtuSheetContainer(
        visible = visible,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Past run #${lap.runNumber}",
                style = BirtuTypography.titleL.copy(color = colors.text),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Duration preview in Timer M
            Text(
                text = TimeEngine.formatCompact(currentDuration),
                style = BirtuTypography.timerM.copy(color = colors.primaryStrong),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Row: Started
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Started", style = BirtuTypography.label.copy(color = colors.text))
                    Text(
                        text = TimeEngine.formatSinceDate(startTimestamp),
                        style = BirtuTypography.caption.copy(color = colors.text2)
                    )
                }

                BirtuTonalButton(
                    text = "Pick",
                    icon = PhosphorIcons.CalendarBlank,
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = startTimestamp }
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
                                        if (chosen < endTimestamp) {
                                            startTimestamp = chosen
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
                        ).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row: Ended
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Ended", style = BirtuTypography.label.copy(color = colors.text))
                    Text(
                        text = TimeEngine.formatSinceDate(endTimestamp),
                        style = BirtuTypography.caption.copy(color = colors.text2)
                    )
                }

                BirtuTonalButton(
                    text = "Pick",
                    icon = PhosphorIcons.CalendarBlank,
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = endTimestamp }
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
                                        if (chosen > startTimestamp) {
                                            endTimestamp = chosen
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
                        }.show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            BirtuPrimaryButton(
                text = "Save",
                enabled = endTimestamp > startTimestamp,
                onClick = {
                    val updated = lap.copy(
                        startEpochMillis = startTimestamp,
                        endEpochMillis = endTimestamp,
                        durationMillis = currentDuration
                    )
                    onSaveLap(updated)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            BirtuTextButton(
                text = "Delete",
                textColor = colors.late,
                onClick = { onDeleteLap(lap.id) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
