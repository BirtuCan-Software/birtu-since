package com.birtucansoftware.birtusince.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.domain.SortOption
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuSegmentedControl
import com.birtucansoftware.birtusince.ui.components.BirtuSheetContainer
import com.birtucansoftware.birtusince.ui.components.BirtuTextButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIconData
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun SortBottomSheet(
    visible: Boolean,
    selectedOption: SortOption,
    isAscending: Boolean,
    selectedFilter: String = "All",
    onOptionSelect: (SortOption) -> Unit,
    onAscendingToggle: (Boolean) -> Unit,
    onFilterSelect: ((String) -> Unit)? = null,
    onClearFilter: (() -> Unit)? = null,
    onDismissRequest: () -> Unit
) {
    val colors = LocalBirtuColors.current

    val hasActiveFilter = selectedFilter != "All"
    val hasActiveCustomSort = selectedOption != SortOption.CREATED_AT || isAscending
    val hasActiveFilterOrSort = hasActiveFilter || hasActiveCustomSort

    BirtuSheetContainer(
        visible = visible,
        onDismissRequest = onDismissRequest
    ) {
        // Header Row: Icon + Title on left, "Clear filter" on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceRaised)
                        .border(1.dp, colors.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    PhosphorIcon(
                        icon = PhosphorIcons.ArrowsDownUp,
                        contentDescription = null,
                        tint = colors.primaryStrong,
                        size = 16.dp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Sort Records",
                    style = BirtuTypography.titleM.copy(
                        color = colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            if (hasActiveFilterOrSort) {
                BirtuTextButton(
                    text = "Clear filter",
                    onClick = {
                        if (onClearFilter != null) {
                            onClearFilter()
                        } else {
                            onOptionSelect(SortOption.CREATED_AT)
                            onAscendingToggle(false)
                            onFilterSelect?.invoke("All")
                        }
                    },
                    textColor = colors.primaryStrong,
                    height = 36.dp
                )
            }
        }

        // Active filter badge if category/status filter is applied
        if (hasActiveFilter) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active filter:",
                    style = BirtuTypography.caption.copy(color = colors.text2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (colors.isDark) colors.surfaceRaised else colors.primarySoft)
                        .border(
                            1.dp,
                            if (colors.isDark) colors.outline else colors.primaryStrong.copy(alpha = 0.35f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onFilterSelect?.invoke("All") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedFilter,
                            style = BirtuTypography.caption.copy(
                                color = colors.primaryStrong,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        PhosphorIcon(
                            icon = PhosphorIcons.X,
                            contentDescription = "Clear filter",
                            tint = colors.primaryStrong,
                            size = 12.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Order direction segmented control
        BirtuSegmentedControl(
            items = listOf(selectedOption.descendingLabel, selectedOption.ascendingLabel),
            selectedIndex = if (isAscending) 1 else 0,
            onItemSelected = { index ->
                onAscendingToggle(index == 1)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Concise sort options list in a single sleek container
        val sortOptions = listOf(
            SortOptionItem(
                option = SortOption.CREATED_AT,
                icon = PhosphorIcons.CalendarBlank
            ),
            SortOptionItem(
                option = SortOption.MOST_LAPS,
                icon = PhosphorIcons.ArrowCounterClockwise
            ),
            SortOptionItem(
                option = SortOption.LAST_MODIFIED,
                icon = PhosphorIcons.PencilSimple
            ),
            SortOptionItem(
                option = SortOption.LONGEST_ELAPSED,
                icon = PhosphorIcons.Hourglass
            )
        )

        val cardShape = RoundedCornerShape(16.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.surfaceRaised)
                .border(1.dp, colors.outline, cardShape)
        ) {
            sortOptions.forEachIndexed { index, item ->
                val isSelected = selectedOption == item.option

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            if (isSelected) {
                                if (colors.isDark) colors.surfaceRaised else colors.primarySoft.copy(alpha = 0.35f)
                            } else {
                                colors.surfaceRaised
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (isSelected) {
                                    onAscendingToggle(!isAscending)
                                } else {
                                    onOptionSelect(item.option)
                                }
                            }
                        )
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhosphorIcon(
                            icon = item.icon,
                            contentDescription = null,
                            tint = if (isSelected) colors.primaryStrong else colors.text2,
                            size = 18.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = item.option.title,
                            style = BirtuTypography.body.copy(
                                color = if (isSelected) colors.text else colors.text2,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }

                    if (isSelected) {
                        PhosphorIcon(
                            icon = PhosphorIcons.Check,
                            contentDescription = "Selected",
                            tint = colors.primaryStrong,
                            size = 18.dp
                        )
                    }
                }

                if (index < sortOptions.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .height(1.dp)
                            .background(colors.outline.copy(alpha = 0.5f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BirtuPrimaryButton(
            text = "Done",
            onClick = onDismissRequest,
            modifier = Modifier.fillMaxWidth(),
            height = 44.dp
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

private data class SortOptionItem(
    val option: SortOption,
    val icon: PhosphorIconData
)
