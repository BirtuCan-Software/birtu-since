package com.birtucansoftware.birtusince.ui.putaway

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuConfirmDialog
import com.birtucansoftware.birtusince.ui.components.BirtuIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuIconTile
import com.birtucansoftware.birtusince.ui.components.birtuCardPress
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun PutAwayScreen(
    items: List<ItemEntity>,
    onItemClick: (Long) -> Unit,
    onBringBackClick: (Long) -> Unit,
    onDeleteItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Put away records",
                    style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.width(4.dp))
                BirtuInfoIconButton(
                    title = "Put Away Records",
                    explanation = "'Put away' archives records so they no longer clutter your home screen. Put-away items pause active notifications, but all past runs and history remain safely saved. You can bring them back at any time."
                )
            }
            Text(
                text = "${items.size} archived",
                style = BirtuTypography.caption.copy(color = colors.text2)
            )
        }

        if (items.isEmpty()) {
            // Empty State (UI Spec 9.7)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(if (colors.isDark) colors.surfaceRaised else colors.primarySoft)
                            .border(
                                1.dp,
                                if (colors.isDark) colors.outline else colors.primaryStrong.copy(alpha = 0.35f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        PhosphorIcon(
                            icon = PhosphorIcons.Archive,
                            contentDescription = null,
                            tint = if (colors.isDark) colors.text2 else colors.primaryStrong,
                            size = 56.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nothing put away",
                        style = BirtuTypography.body.copy(color = colors.text2)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    val derived = ItemStateResolver.resolve(
                        startEpochMillis = item.startEpochMillis,
                        isPaused = true,
                        pauseEpochMillis = item.pauseTimestamp ?: item.startEpochMillis,
                        isArchived = true,
                        isPrivate = false,
                        isUnlockedThisSession = true,
                        targetIntervalMillis = item.targetIntervalMillis,
                        unit = item.preferredUnit
                    )

                    // Put away card look: fill bg, 1 dp outline, timer frozen, Archive icon on right
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.bg)
                            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
                            .birtuCardPress(onClick = { onItemClick(item.id) })
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BirtuIconTile(
                                icon = PhosphorIcons.fromName(item.categoryIcon),
                                backgroundColor = colors.surfaceHigh,
                                tint = colors.text2,
                                size = 48.dp,
                                iconSize = 24.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = BirtuTypography.titleM.copy(color = colors.text),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = derived.formattedDuration.primaryText,
                                    style = BirtuTypography.timerM.copy(color = colors.text2)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Since ${TimeEngine.formatSinceDate(item.startEpochMillis)}",
                                    style = BirtuTypography.caption.copy(color = colors.text2)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Bring back button (36 dp)
                            BirtuIconButton(
                                icon = PhosphorIcons.Archive,
                                contentDescription = "Bring back",
                                onClick = { onBringBackClick(item.id) },
                                backgroundColor = colors.surfaceRaised,
                                tint = colors.primaryStrong,
                                size = 40.dp,
                                iconSize = 20.dp
                            )
                        }
                    }
                }
            }
        }
    }

    itemToDelete?.let { item ->
        BirtuConfirmDialog(
            visible = true,
            title = "Delete this?",
            body = "It and all its past runs will be gone for good.",
            confirmText = "Delete",
            cancelText = "Keep",
            onConfirm = {
                onDeleteItemClick(item.id)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null },
            isDanger = true
        )
    }
}
