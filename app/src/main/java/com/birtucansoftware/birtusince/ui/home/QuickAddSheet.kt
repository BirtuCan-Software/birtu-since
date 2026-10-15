package com.birtucansoftware.birtusince.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuChip
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuSheetContainer
import com.birtucansoftware.birtusince.ui.components.BirtuSwitch
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
fun QuickAddSheet(
    visible: Boolean,
    onDismissRequest: (draft: String) -> Unit,
    onStartItem: (
        title: String,
        startEpochMillis: Long,
        category: String,
        categoryIcon: String,
        notes: String,
        targetIntervalMillis: Long?,
        isPrivate: Boolean
    ) -> Unit,
    existingGroups: List<String> = emptyList(),
    initialDraft: String = ""
) {
    val context = LocalContext.current
    val colors = LocalBirtuColors.current

    var title by remember(visible, initialDraft) { mutableStateOf(initialDraft) }
    var startTimestamp by remember(visible) { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedGroup by remember(visible) { mutableStateOf("") }
    var selectedGroupIcon by remember(visible) { mutableStateOf("Timer") }
    var reminderIntervalMillis by remember(visible) { mutableStateOf<Long?>(null) }
    var isPrivate by remember(visible) { mutableStateOf(false) }
    var noteText by remember(visible) { mutableStateOf("") }

    // Expanded option sub-panels: "started", "group", "remind", "note", or null
    var activeSubPanel by remember(visible) { mutableStateOf<String?>(null) }

    // Remind me state
    var reminderValue by remember { mutableIntStateOf(14) }
    var reminderUnitIndex by remember { mutableIntStateOf(2) } // 0: Min, 1: Hours, 2: Days

    fun calculateReminderMillis(value: Int, unitIdx: Int): Long {
        val factor = when (unitIdx) {
            0 -> 60_000L
            1 -> 3_600_000L
            else -> 86_400_000L
        }
        return value * factor
    }

    BirtuSheetContainer(
        visible = visible,
        onDismissRequest = { onDismissRequest(title) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "New",
                style = BirtuTypography.titleL.copy(color = colors.text),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Text field (mandatory title, max 120 chars)
            BirtuTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Watered the plants",
                label = "What happened?",
                maxCharacters = 120,
                onDone = {
                    if (title.isNotBlank()) {
                        onStartItem(
                            title.trim(),
                            startTimestamp,
                            selectedGroup,
                            selectedGroupIcon,
                            noteText.trim(),
                            reminderIntervalMillis,
                            isPrivate
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Option chips row
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Started chip
                val startedLabel = if (Math.abs(System.currentTimeMillis() - startTimestamp) < 60_000) {
                    "Started: Now"
                } else {
                    "Started: ${TimeEngine.formatSinceDate(startTimestamp)}"
                }
                BirtuChip(
                    label = startedLabel,
                    selected = activeSubPanel == "started" || startTimestamp < System.currentTimeMillis() - 60_000,
                    icon = PhosphorIcons.CalendarBlank,
                    onClick = {
                        activeSubPanel = if (activeSubPanel == "started") null else "started"
                    }
                )

                // 2. Group chip
                val groupLabel = if (selectedGroup.isNotEmpty()) selectedGroup else "Group"
                BirtuChip(
                    label = groupLabel,
                    selected = activeSubPanel == "group" || selectedGroup.isNotEmpty(),
                    icon = PhosphorIcons.Tag,
                    onClick = {
                        activeSubPanel = if (activeSubPanel == "group") null else "group"
                    }
                )

                // 3. Remind me chip
                val remindLabel = if (reminderIntervalMillis != null) {
                    val unitName = when (reminderUnitIndex) {
                        0 -> "min"
                        1 -> "hours"
                        else -> "days"
                    }
                    "Every $reminderValue $unitName"
                } else {
                    "Remind me"
                }
                BirtuChip(
                    label = remindLabel,
                    selected = activeSubPanel == "remind" || reminderIntervalMillis != null,
                    icon = PhosphorIcons.Bell,
                    onClick = {
                        activeSubPanel = if (activeSubPanel == "remind") null else "remind"
                    }
                )

                // 4. Lock it chip
                BirtuChip(
                    label = "Lock it",
                    selected = isPrivate,
                    icon = if (isPrivate) PhosphorIcons.Lock else PhosphorIcons.LockOpen,
                    onClick = {
                        val targetPrivate = !isPrivate
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            BiometricAuthManager.authenticate(
                                activity = activity,
                                title = if (targetPrivate) "Lock Item" else "Unlock Item",
                                subtitle = if (targetPrivate) "Authenticate to lock this record" else "Authenticate to unlock this record",
                                onSuccess = { isPrivate = targetPrivate },
                                onError = {}
                            )
                        } else {
                            isPrivate = targetPrivate
                        }
                    }
                )

                // 5. Note chip
                BirtuChip(
                    label = if (noteText.isNotEmpty()) "Note (${noteText.length})" else "Note",
                    selected = activeSubPanel == "note" || noteText.isNotEmpty(),
                    icon = PhosphorIcons.Note,
                    onClick = {
                        activeSubPanel = if (activeSubPanel == "note") null else "note"
                    }
                )
            }

            // Sub-panels (expand smoothly with animation)
            AnimatedVisibility(
                visible = activeSubPanel != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceRaised)
                        .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    when (activeSubPanel) {
                        "started" -> {
                            Column {
                                Text(
                                    text = "When did it happen?",
                                    style = BirtuTypography.label.copy(color = colors.text)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    BirtuTonalButton(
                                        text = "Now",
                                        onClick = {
                                            startTimestamp = System.currentTimeMillis()
                                            activeSubPanel = null
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    BirtuTonalButton(
                                        text = "1 hour ago",
                                        onClick = {
                                            startTimestamp = System.currentTimeMillis() - (60 * 60 * 1000)
                                            activeSubPanel = null
                                        },
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    BirtuTonalButton(
                                        text = "Yesterday",
                                        onClick = {
                                            startTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                                            activeSubPanel = null
                                        },
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                BirtuTonalButton(
                                    text = "Pick time",
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
                                                        if (chosen <= System.currentTimeMillis()) {
                                                            startTimestamp = chosen
                                                            activeSubPanel = null
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
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        "group" -> {
                            Column {
                                Text(
                                    text = "Choose group or icon",
                                    style = BirtuTypography.label.copy(color = colors.text)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                BirtuTextField(
                                    value = selectedGroup,
                                    onValueChange = { selectedGroup = it },
                                    placeholder = "e.g. Home, Health, Car",
                                    label = "Group name (optional)",
                                    maxCharacters = 30
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                GroupIconPickerRow(
                                    selectedIconName = selectedGroupIcon,
                                    onIconSelected = { selectedGroupIcon = it }
                                )
                            }
                        }

                        "remind" -> {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Remind me after",
                                            style = BirtuTypography.label.copy(color = colors.text)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        BirtuInfoIconButton(
                                            title = "Remind me",
                                            explanation = "Set a target interval (e.g. 14 days) to receive a local notification when your elapsed timer reaches or passes that target."
                                        )
                                    }
                                    BirtuSwitch(
                                        checked = reminderIntervalMillis != null,
                                        onCheckedChange = { on ->
                                            reminderIntervalMillis = if (on) {
                                                calculateReminderMillis(reminderValue, reminderUnitIndex)
                                            } else null
                                        }
                                    )
                                }

                                if (reminderIntervalMillis != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ReminderIntervalPicker(
                                        value = reminderValue,
                                        unitIndex = reminderUnitIndex,
                                        onValueChange = { newVal ->
                                            reminderValue = newVal
                                            reminderIntervalMillis = calculateReminderMillis(newVal, reminderUnitIndex)
                                        },
                                        onUnitIndexChange = { newIdx ->
                                            reminderUnitIndex = newIdx
                                            reminderIntervalMillis = calculateReminderMillis(reminderValue, newIdx)
                                        }
                                    )
                                }
                            }
                        }

                        "note" -> {
                            Column {
                                Text(
                                    text = "Add note (optional)",
                                    style = BirtuTypography.label.copy(color = colors.text)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BirtuTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    placeholder = "Extra details, reminders or links...",
                                    label = "Note",
                                    maxCharacters = 2000,
                                    singleLine = false,
                                    minHeight = 88.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Start button (56 dp tall)
            BirtuPrimaryButton(
                text = "Start",
                icon = PhosphorIcons.Plus,
                enabled = title.isNotBlank(),
                onClick = {
                    if (title.isNotBlank()) {
                        onStartItem(
                            title.trim(),
                            startTimestamp,
                            selectedGroup,
                            selectedGroupIcon,
                            noteText.trim(),
                            reminderIntervalMillis,
                            isPrivate
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
