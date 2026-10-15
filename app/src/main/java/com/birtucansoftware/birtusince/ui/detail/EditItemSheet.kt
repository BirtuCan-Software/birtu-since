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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuSheetContainer
import com.birtucansoftware.birtusince.ui.components.BirtuSwitch
import com.birtucansoftware.birtusince.ui.components.BirtuTextButton
import com.birtucansoftware.birtusince.ui.components.BirtuTextField
import com.birtucansoftware.birtusince.ui.components.BirtuTonalButton
import com.birtucansoftware.birtusince.ui.components.GroupIconPickerRow
import com.birtucansoftware.birtusince.ui.components.ReminderIntervalPicker
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.lock.BiometricAuthManager
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import java.util.Calendar

@Composable
fun EditItemSheet(
    visible: Boolean,
    item: ItemEntity,
    decryptedTitle: String,
    decryptedNotes: String,
    onDismissRequest: () -> Unit,
    onSaveItem: (updatedItem: ItemEntity) -> Unit,
    onDeleteItem: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    var title by remember(item, visible) { mutableStateOf(decryptedTitle) }
    var startTimestamp by remember(item, visible) { mutableLongStateOf(item.startEpochMillis) }
    var category by remember(item, visible) { mutableStateOf(item.category) }
    var categoryIcon by remember(item, visible) { mutableStateOf(item.categoryIcon) }
    var notes by remember(item, visible) { mutableStateOf(decryptedNotes) }
    var isPrivate by remember(item, visible) { mutableStateOf(item.isPrivate) }

    var isRemindEnabled by remember(item, visible) { mutableStateOf(item.targetIntervalMillis != null) }
    var reminderValue by remember(item, visible) {
        val millis = item.targetIntervalMillis ?: (14 * 86_400_000L)
        val days = (millis / 86_400_000L).toInt()
        val hours = (millis / 3_600_000L).toInt()
        val mins = (millis / 60_000L).toInt()
        mutableIntStateOf(
            when {
                days >= 1 -> days
                hours >= 1 -> hours
                mins >= 1 -> mins
                else -> 14
            }
        )
    }
    var reminderUnitIndex by remember(item, visible) {
        val millis = item.targetIntervalMillis ?: (14 * 86_400_000L)
        val days = millis / 86_400_000L
        val hours = millis / 3_600_000L
        mutableIntStateOf(
            when {
                days >= 1 -> 2 // Days
                hours >= 1 -> 1 // Hours
                else -> 0 // Min
            }
        )
    }

    BirtuSheetContainer(
        visible = visible,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Edit",
                style = BirtuTypography.titleL.copy(color = colors.text),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            BirtuTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Title",
                label = "What happened?",
                maxCharacters = 120
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Started row
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
                    text = "Change",
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
                                        if (chosen <= System.currentTimeMillis()) {
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
                        ).apply {
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            BirtuTextField(
                value = category,
                onValueChange = { category = it },
                placeholder = "Group name",
                label = "Group (optional)",
                maxCharacters = 30
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reusable Icon picker for group
            GroupIconPickerRow(
                selectedIconName = categoryIcon,
                onIconSelected = { categoryIcon = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            BirtuTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = "Notes...",
                label = "Note",
                maxCharacters = 2000,
                singleLine = false,
                minHeight = 80.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Remind me on target row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Remind me on target", style = BirtuTypography.label.copy(color = colors.text))
                        Spacer(modifier = Modifier.width(4.dp))
                        BirtuInfoIconButton(
                            title = "Remind me on target",
                            explanation = "Receive local notifications on this device when your elapsed timer reaches or passes your target duration."
                        )
                    }
                    BirtuSwitch(checked = isRemindEnabled, onCheckedChange = { isRemindEnabled = it })
                }

                if (isRemindEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ReminderIntervalPicker(
                        value = reminderValue,
                        unitIndex = reminderUnitIndex,
                        onValueChange = { reminderValue = it },
                        onUnitIndexChange = { reminderUnitIndex = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Lock it", style = BirtuTypography.label.copy(color = colors.text))
                    Spacer(modifier = Modifier.width(4.dp))
                    BirtuInfoIconButton(
                        title = "Lock it",
                        explanation = "Locking a record encrypts its title and notes on device using hardware Keystore encryption. Sensitive details are hidden on screen until unlocked with fingerprint, face, or PIN."
                    )
                }
                BirtuSwitch(
                    checked = isPrivate,
                    onCheckedChange = { checked ->
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            BiometricAuthManager.authenticate(
                                activity = activity,
                                title = if (checked) "Lock Item" else "Unlock Item",
                                subtitle = if (checked) "Authenticate to lock this record" else "Authenticate to unlock this record",
                                onSuccess = { isPrivate = checked },
                                onError = {}
                            )
                        } else {
                            isPrivate = checked
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            BirtuPrimaryButton(
                text = "Save",
                enabled = title.isNotBlank(),
                onClick = {
                    val finalTarget = if (isRemindEnabled) {
                        val factor = when (reminderUnitIndex) {
                            0 -> 60_000L
                            1 -> 3_600_000L
                            else -> 86_400_000L
                        }
                        reminderValue * factor
                    } else null

                    val updated = item.copy(
                        title = title.trim(),
                        startEpochMillis = startTimestamp,
                        category = category.trim(),
                        categoryIcon = categoryIcon,
                        notes = notes.trim(),
                        targetIntervalMillis = finalTarget,
                        isPrivate = isPrivate
                    )
                    onSaveItem(updated)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            BirtuTextButton(
                text = "Delete",
                textColor = colors.late,
                onClick = onDeleteItem,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
