package com.birtucansoftware.birtusince.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.data.LapEntity
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.PatternStatsEngine
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuConfirmDialog
import com.birtucansoftware.birtusince.ui.components.BirtuDangerButton
import com.birtucansoftware.birtusince.ui.components.BirtuIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuTonalButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun ItemDetailScreen(
    item: ItemEntity,
    laps: List<LapEntity>,
    currentTick: Long,
    decryptedTitle: String,
    decryptedNotes: String,
    onBackClick: () -> Unit,
    onStartOverNow: () -> Unit,
    onStartOverPast: (epochMillis: Long) -> Unit,
    onPauseResumeClick: () -> Unit,
    onPutAwayClick: () -> Unit,
    onBringBackClick: () -> Unit,
    onDeleteItemClick: () -> Unit,
    onUnitChanged: (String) -> Unit,
    onUpdateItem: (ItemEntity) -> Unit,
    onSaveLap: (LapEntity) -> Unit,
    onDeleteLap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    var showMoreMenu by remember { mutableStateOf(false) }
    var showStartOverSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedLapToEdit by remember { mutableStateOf<LapEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val derived = remember(item, currentTick) {
        ItemStateResolver.resolve(
            startEpochMillis = item.startEpochMillis,
            isPaused = item.isPaused,
            pauseEpochMillis = item.pauseTimestamp,
            isArchived = item.isArchived,
            isPrivate = item.isPrivate,
            isUnlockedThisSession = true, // Screen opened means unlocked
            targetIntervalMillis = item.targetIntervalMillis,
            unit = item.preferredUnit,
            nowEpochMillis = currentTick
        )
    }

    val isLate = derived.state is ItemState.Late
    val isPaused = derived.state is ItemState.Paused
    val isClockOff = derived.state is ItemState.ClockLooksOff
    val isArchived = item.isArchived

    // Units: Auto, Days, Hours, Min, Sec
    val unitList = listOf("Auto", "Days", "Hours", "Min", "Sec")
    val currentUnitIndex = remember(item.preferredUnit) {
        when (item.preferredUnit.lowercase()) {
            TimeEngine.UNIT_DAYS -> 1
            TimeEngine.UNIT_HOURS -> 2
            TimeEngine.UNIT_MIN -> 3
            TimeEngine.UNIT_SEC -> 4
            else -> 0
        }
    }

    // Pattern stats calculation (for >= 2 historical laps)
    val patternStats = remember(laps, item.targetIntervalMillis) {
        val durations = laps.map { it.durationMillis }
        PatternStatsEngine.calculate(durations, item.targetIntervalMillis)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BirtuIconButton(
                icon = PhosphorIcons.CaretLeft,
                contentDescription = "Back",
                onClick = onBackClick
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isArchived) {
                    BirtuIconButton(
                        icon = PhosphorIcons.PencilSimple,
                        contentDescription = "Edit",
                        onClick = { showEditSheet = true }
                    )
                }

                Box {
                    BirtuIconButton(
                        icon = PhosphorIcons.DotsThreeVertical,
                        contentDescription = "More",
                        onClick = { showMoreMenu = true }
                    )

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        if (!isArchived) {
                            DropdownMenuItem(
                                text = { Text(if (isPaused) "Resume" else "Pause") },
                                onClick = {
                                    showMoreMenu = false
                                    onPauseResumeClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Put away") },
                                onClick = {
                                    showMoreMenu = false
                                    onPutAwayClick()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Bring back") },
                                onClick = {
                                    showMoreMenu = false
                                    onBringBackClick()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete", color = colors.late) },
                            onClick = {
                                showMoreMenu = false
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Scrollable Screen Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Group Chip if set
            val hasCategory = item.category.isNotBlank()
            val hasIcon = item.categoryIcon.isNotBlank()
            if (hasCategory || hasIcon) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(colors.surfaceHigh)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasIcon) {
                            PhosphorIcon(
                                icon = PhosphorIcons.fromName(item.categoryIcon),
                                contentDescription = item.category.ifBlank { item.categoryIcon },
                                tint = colors.primaryStrong,
                                size = 14.dp
                            )
                        }
                        if (hasCategory && hasIcon) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (hasCategory) {
                            Text(
                                text = item.category,
                                style = BirtuTypography.caption.copy(
                                    color = colors.text2,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Title (up to 2 lines)
            Text(
                text = decryptedTitle,
                style = BirtuTypography.titleL.copy(color = colors.text),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clock looks off banner if anomaly detected
            if (isClockOff) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceHigh)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PhosphorIcon(
                        icon = PhosphorIcons.Warning,
                        contentDescription = null,
                        tint = colors.late,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clock looks off. We will catch up.",
                        style = BirtuTypography.body.copy(color = colors.text, fontSize = 13.sp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Hero Timer Panel
            ItemDetailHeroPanel(
                derived = derived,
                targetIntervalMillis = item.targetIntervalMillis,
                startEpochMillis = item.startEpochMillis,
                preferredUnit = item.preferredUnit,
                currentUnitIndex = currentUnitIndex,
                unitList = unitList,
                isArchived = isArchived,
                isPaused = isPaused,
                isLate = isLate,
                onUnitChanged = onUnitChanged
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            if (!isArchived) {
                if (!item.isOneTime) {
                    BirtuPrimaryButton(
                        text = "Start over",
                        icon = PhosphorIcons.ArrowCounterClockwise,
                        onClick = { showStartOverSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BirtuTonalButton(
                        text = if (isPaused) "Resume" else "Pause",
                        icon = if (isPaused) PhosphorIcons.Play else PhosphorIcons.Pause,
                        onClick = onPauseResumeClick,
                        modifier = Modifier.weight(1f)
                    )

                    BirtuTonalButton(
                        text = "Edit",
                        icon = PhosphorIcons.PencilSimple,
                        onClick = { showEditSheet = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BirtuTonalButton(
                        text = "Bring back",
                        icon = PhosphorIcons.Archive,
                        onClick = onBringBackClick,
                        modifier = Modifier.weight(1f)
                    )
                    BirtuDangerButton(
                        text = "Delete",
                        icon = PhosphorIcons.Trash,
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information & Settings Section (Target reminder, Group, Note, Lock, One-time event)
            ItemDetailInfoSection(
                item = item,
                decryptedTitle = decryptedTitle,
                decryptedNotes = decryptedNotes,
                onEditTargetClick = { showEditSheet = true },
                onUpdateItem = onUpdateItem
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Your Pattern Analytics & Past Runs Section (only for repeatable events with history)
            if (!item.isOneTime) {
                PatternStatsSection(
                    patternStats = patternStats,
                    targetIntervalMillis = item.targetIntervalMillis
                )

                Spacer(modifier = Modifier.height(28.dp))

                ItemDetailPastRunsSection(
                    laps = laps,
                    onLapClick = { selectedLapToEdit = it }
                )

                Spacer(modifier = Modifier.height(32.dp))
            } else if (laps.isNotEmpty()) {
                ItemDetailPastRunsSection(
                    laps = laps,
                    onLapClick = { selectedLapToEdit = it }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Sheets & Dialogs
    StartOverSheet(
        visible = showStartOverSheet,
        runDurationCompact = TimeEngine.formatCompact(derived.elapsedMillis),
        onDismissRequest = { showStartOverSheet = false },
        onStartOverNow = {
            showStartOverSheet = false
            onStartOverNow()
        },
        onStartOverPast = { pastEpoch ->
            showStartOverSheet = false
            onStartOverPast(pastEpoch)
        },
        minAllowedStartMillis = item.startEpochMillis
    )

    if (showEditSheet) {
        EditItemSheet(
            visible = true,
            item = item,
            decryptedTitle = decryptedTitle,
            decryptedNotes = decryptedNotes,
            onDismissRequest = { showEditSheet = false },
            onSaveItem = { updated ->
                showEditSheet = false
                onUpdateItem(updated)
            },
            onDeleteItem = {
                showEditSheet = false
                showDeleteConfirmDialog = true
            }
        )
    }

    selectedLapToEdit?.let { lap ->
        EditPastRunSheet(
            visible = true,
            lap = lap,
            onDismissRequest = { selectedLapToEdit = null },
            onSaveLap = { updated ->
                selectedLapToEdit = null
                onSaveLap(updated)
            },
            onDeleteLap = { lapId ->
                selectedLapToEdit = null
                onDeleteLap(lapId)
            }
        )
    }

    BirtuConfirmDialog(
        visible = showDeleteConfirmDialog,
        title = "Delete this?",
        body = "It and all its past runs will be gone for good.",
        confirmText = "Delete",
        cancelText = "Keep",
        onConfirm = {
            showDeleteConfirmDialog = false
            onDeleteItemClick()
        },
        onDismiss = { showDeleteConfirmDialog = false },
        isDanger = true
    )
}
