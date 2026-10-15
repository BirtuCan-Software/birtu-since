package com.birtucansoftware.birtusince.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.data.KeystoreCrypto
import com.birtucansoftware.birtusince.domain.ItemDerivedStatus
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuCard
import com.birtucansoftware.birtusince.ui.components.BirtuIconButton
import com.birtucansoftware.birtusince.ui.components.CardProgressRing
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun HomeItemCard(
    item: ItemEntity,
    derived: ItemDerivedStatus,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    onStartOver: () -> Unit,
    onPauseResume: () -> Unit,
    onPutAway: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    var showMenu by remember { mutableStateOf(false) }

    val isLocked = item.isPrivate && !isUnlocked
    val isLate = derived.state is ItemState.Late
    val isPaused = derived.state is ItemState.Paused

    val borderColor = if (isLate) colors.late else colors.outline

    Box(modifier = modifier) {
        BirtuCard(
            onClick = onClick,
            onLongClick = { showMenu = true },
            borderColor = borderColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Group: Title + Subtitle/Timers
                Column(modifier = Modifier.weight(1f)) {
                    if (isLocked) {
                        val displayTitle = if (item.title == "Locked Item" && !item.encryptedTitle.isNullOrBlank()) {
                            KeystoreCrypto.decrypt(item.encryptedTitle)
                        } else {
                            item.title
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhosphorIcon(
                                icon = PhosphorIcons.Lock,
                                contentDescription = "Locked",
                                tint = colors.primary,
                                size = 16.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = displayTitle,
                                style = BirtuTypography.titleM.copy(color = colors.text),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.blur(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Tap to unlock",
                            style = BirtuTypography.caption.copy(color = colors.text2)
                        )
                    } else {
                        Text(
                            text = item.title,
                            style = BirtuTypography.titleM.copy(color = colors.text),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        val timerColor = when {
                            isLate -> colors.late
                            isPaused -> colors.text2
                            else -> colors.primaryStrong
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = derived.formattedDuration.primaryText,
                                style = BirtuTypography.timerM.copy(
                                    color = timerColor,
                                    fontSize = 18.sp
                                )
                            )

                            if (isPaused) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.surfaceRaised)
                                        .border(1.dp, colors.outline, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Paused",
                                        style = BirtuTypography.caption.copy(color = colors.text2, fontSize = 10.sp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtitle row: "Late" / Time left + Group Badge next to it
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isLate && item.targetIntervalMillis != null) {
                                Text(
                                    text = "Late",
                                    style = BirtuTypography.caption.copy(color = colors.late, fontWeight = FontWeight.SemiBold)
                                )
                            } else if (item.targetIntervalMillis != null) {
                                val remainingMillis = (item.targetIntervalMillis - derived.elapsedMillis).coerceAtLeast(0L)
                                Text(
                                    text = "${TimeEngine.formatCompact(remainingMillis)} left",
                                    style = BirtuTypography.caption.copy(color = colors.text2)
                                )
                            }

                            val hasCategory = item.category.isNotBlank()
                            val hasIcon = item.categoryIcon.isNotBlank()
                            if ((hasCategory || hasIcon) && !isLocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.surfaceRaised)
                                        .border(1.dp, colors.outline, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (hasIcon) {
                                            PhosphorIcon(
                                                icon = PhosphorIcons.fromName(item.categoryIcon),
                                                contentDescription = item.category.ifBlank { item.categoryIcon },
                                                tint = colors.primaryStrong,
                                                size = 12.dp
                                            )
                                        }
                                        if (hasCategory && hasIcon) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        if (hasCategory) {
                                            Text(
                                                text = item.category,
                                                style = BirtuTypography.caption.copy(
                                                    color = colors.text2,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Group: Progress Ring + Quick Start Over circle button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // If has target interval and not locked, show mini progress ring
                    if (!isLocked && item.targetIntervalMillis != null) {
                        CardProgressRing(
                            progressFraction = derived.progressFraction,
                            isLate = isLate,
                            size = 32.dp
                        ) {}
                    }

                    // Quick Start Over circle button
                    if (!isLocked) {
                        BirtuIconButton(
                            icon = PhosphorIcons.ArrowCounterClockwise,
                            contentDescription = "Start over",
                            onClick = onStartOver,
                            size = 38.dp,
                            iconSize = 20.dp,
                            backgroundColor = colors.surfaceRaised,
                            borderColor = colors.outline
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (isPaused) "Resume" else "Pause") },
                onClick = {
                    showMenu = false
                    onPauseResume()
                }
            )
            DropdownMenuItem(
                text = { Text("Put away") },
                onClick = {
                    showMenu = false
                    onPutAway()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = colors.late) },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}
