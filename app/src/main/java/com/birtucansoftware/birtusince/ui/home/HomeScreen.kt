package com.birtucansoftware.birtusince.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.ui.components.BirtuChip
import com.birtucansoftware.birtusince.ui.components.BirtuIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.components.BirtuTextButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun HomeScreen(
    items: List<ItemEntity>,
    currentTick: Long,
    unlockedItemIds: Set<Long>,
    selectedFilter: String = "All",
    onFilterSelect: (String) -> Unit = {},
    sortOption: com.birtucansoftware.birtusince.domain.SortOption = com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT,
    isSortAscending: Boolean = false,
    onSortOptionSelect: (com.birtucansoftware.birtusince.domain.SortOption) -> Unit = {},
    onSortAscendingToggle: (Boolean) -> Unit = {},
    onTogglePinClick: (Long) -> Unit = {},
    onItemClick: (Long) -> Unit,
    onStartOverClick: (Long) -> Unit,
    onPauseResumeClick: (Long) -> Unit,
    onPutAwayClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onUnlockClick: (Long) -> Unit,
    onHelpClick: () -> Unit = {},
    searchBarModifier: Modifier = Modifier,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }

    // Resolve derived status for items
    val resolvedItems = remember(items, currentTick, unlockedItemIds) {
        items.map { item ->
            val isUnlocked = unlockedItemIds.contains(item.id)
            val derived = ItemStateResolver.resolve(
                startEpochMillis = item.startEpochMillis,
                isPaused = item.isPaused,
                pauseEpochMillis = item.pauseTimestamp,
                isArchived = item.isArchived,
                isPrivate = item.isPrivate,
                isUnlockedThisSession = isUnlocked,
                targetIntervalMillis = item.targetIntervalMillis,
                unit = item.preferredUnit,
                nowEpochMillis = currentTick
            )
            item to derived
        }
    }

    // Counts for summary line
    val runningCount = remember(resolvedItems) {
        resolvedItems.count { it.second.state is ItemState.Running || it.second.state is ItemState.Late }
    }
    val lateCount = remember(resolvedItems) {
        resolvedItems.count { it.second.state is ItemState.Late }
    }

    // Dynamic groups from items
    val groups = remember(items) {
        items.map { it.category }.filter { it.isNotBlank() }.distinct()
    }

    // Filter and search
    val filteredItems = remember(resolvedItems, selectedFilter, searchQuery) {
        resolvedItems.filter { (item, derived) ->
            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Late" -> derived.state is ItemState.Late
                else -> item.category.equals(selectedFilter, ignoreCase = true)
            }
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else if (item.isPrivate && !unlockedItemIds.contains(item.id)) {
                false // Don't search locked titles
            } else {
                item.title.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }

    // Sort items: Pinned items always appear at the top, then sorted by chosen criterion
    val sortedAndFilteredItems = remember(filteredItems, sortOption, isSortAscending, currentTick) {
        filteredItems.sortedWith { a, b ->
            val itemA = a.first
            val itemB = b.first
            val derivedA = a.second
            val derivedB = b.second

            // Pinned items always come first!
            if (itemA.isPinned != itemB.isPinned) {
                return@sortedWith if (itemA.isPinned) -1 else 1
            }

            val comparison = when (sortOption) {
                com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT -> {
                    itemA.createdAtEpochMillis.compareTo(itemB.createdAtEpochMillis)
                }
                com.birtucansoftware.birtusince.domain.SortOption.MOST_LAPS -> {
                    itemA.lifetimeRestarts.compareTo(itemB.lifetimeRestarts)
                }
                com.birtucansoftware.birtusince.domain.SortOption.LAST_MODIFIED -> {
                    val modA = if (itemA.lastModifiedEpochMillis > 0) itemA.lastModifiedEpochMillis else itemA.createdAtEpochMillis
                    val modB = if (itemB.lastModifiedEpochMillis > 0) itemB.lastModifiedEpochMillis else itemB.createdAtEpochMillis
                    modA.compareTo(modB)
                }
                com.birtucansoftware.birtusince.domain.SortOption.LONGEST_ELAPSED -> {
                    derivedA.elapsedMillis.compareTo(derivedB.elapsedMillis)
                }
            }

            if (isSortAscending) comparison else -comparison
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        // Search bar & Sort icon button on the same line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .then(searchBarModifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar Input
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceRaised)
                    .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhosphorIcon(
                    icon = PhosphorIcons.MagnifyingGlass,
                    contentDescription = null,
                    tint = colors.text2,
                    size = 20.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    textStyle = BirtuTypography.body.copy(color = colors.text),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = BirtuTypography.body.copy(color = colors.text2)
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    BirtuIconButton(
                        icon = PhosphorIcons.X,
                        contentDescription = "Clear search",
                        size = 32.dp,
                        iconSize = 16.dp,
                        onClick = { searchQuery = "" }
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort icon button on the same line as the search bar but on its right
            val isCustomSortActive = sortOption != com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT || isSortAscending
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isCustomSortActive) {
                            if (colors.isDark) colors.surfaceRaised else colors.primarySoft
                        } else {
                            colors.surfaceRaised
                        }
                    )
                    .border(
                        1.dp,
                        if (isCustomSortActive) colors.primaryStrong else colors.outline,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { showSortSheet = true }
                    ),
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(
                    icon = PhosphorIcons.ArrowsDownUp,
                    contentDescription = "Sort records",
                    tint = if (isCustomSortActive) colors.primaryStrong else colors.text,
                    size = 20.dp
                )
            }
        }

        // Summary Line: e.g. "3 running   1 late"
        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$runningCount",
                        style = BirtuTypography.caption.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = " running   ",
                        style = BirtuTypography.caption.copy(color = colors.text2)
                    )

                    if (lateCount > 0) {
                        Text(
                            text = "$lateCount",
                            style = BirtuTypography.caption.copy(color = colors.late, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = " late",
                            style = BirtuTypography.caption.copy(color = colors.late)
                        )
                    }
                }

                BirtuInfoIconButton(
                    title = "BirtuSince Overview",
                    explanation = "BirtuSince tracks elapsed time since your events happened. Running timers count upwards continuously. Records with targets turn red when past their target duration."
                )
            }
        }

        // Filter chips (scrolls sideways)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            BirtuChip(
                label = "All",
                selected = selectedFilter == "All",
                onClick = { onFilterSelect("All") }
            )
            BirtuChip(
                label = "Late",
                selected = selectedFilter == "Late",
                onClick = { onFilterSelect("Late") }
            )
            groups.forEach { groupName ->
                BirtuChip(
                    label = groupName,
                    selected = selectedFilter.equals(groupName, ignoreCase = true),
                    onClick = { onFilterSelect(groupName) }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // List Content or Empty State with smooth filter transition
        AnimatedContent(
            targetState = if (isLoading) "loading" else selectedFilter,
            transitionSpec = {
                (fadeIn(animationSpec = tween(180)) + slideInVertically(animationSpec = tween(180)) { it / 8 })
                    .togetherWith(fadeOut(animationSpec = tween(120)))
            },
            label = "FilterChipSwitchAnimation",
            modifier = Modifier.fillMaxSize()
        ) { targetFilter ->
            if (targetFilter == "loading") {
                HomeScreenLoadingSkeleton()
            } else if (items.isEmpty()) {
                // Home Empty State
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
                                icon = PhosphorIcons.Hourglass,
                                contentDescription = null,
                                tint = if (colors.isDark) colors.text2 else colors.primaryStrong,
                                size = 56.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Nothing yet",
                            style = BirtuTypography.titleL.copy(
                                color = colors.text,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap New to start.",
                            style = BirtuTypography.body.copy(color = colors.text2)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        BirtuTextButton(
                            text = stringResource(R.string.help_tour_title),
                            onClick = onHelpClick
                        )
                    }
                }
            } else if (sortedAndFilteredItems.isEmpty()) {
                // No search or filter match
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
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
                                icon = PhosphorIcons.MagnifyingGlass,
                                contentDescription = null,
                                tint = if (colors.isDark) colors.text2 else colors.primaryStrong,
                                size = 48.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No match",
                            style = BirtuTypography.titleM.copy(color = colors.text)
                        )
                        if (searchQuery.isNotEmpty() || selectedFilter != "All" || sortOption != com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT || isSortAscending) {
                            Spacer(modifier = Modifier.height(8.dp))
                            BirtuTextButton(
                                text = "Clear filter",
                                onClick = {
                                    searchQuery = ""
                                    onFilterSelect("All")
                                    onSortOptionSelect(com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT)
                                    onSortAscendingToggle(false)
                                }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = sortedAndFilteredItems,
                        key = { it.first.id }
                    ) { (item, derived) ->
                        val isUnlocked = unlockedItemIds.contains(item.id)
                        HomeItemCard(
                            item = item,
                            derived = derived,
                            isUnlocked = isUnlocked,
                            onClick = {
                                if (item.isPrivate && !isUnlocked) {
                                    onUnlockClick(item.id)
                                } else {
                                    onItemClick(item.id)
                                }
                            },
                            onStartOver = { onStartOverClick(item.id) },
                            onPauseResume = { onPauseResumeClick(item.id) },
                            onPutAway = { onPutAwayClick(item.id) },
                            onDelete = { onDeleteClick(item.id) },
                            onTogglePin = { onTogglePinClick(item.id) }
                        )
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    SortBottomSheet(
        visible = showSortSheet,
        selectedOption = sortOption,
        isAscending = isSortAscending,
        selectedFilter = selectedFilter,
        onOptionSelect = onSortOptionSelect,
        onAscendingToggle = onSortAscendingToggle,
        onFilterSelect = onFilterSelect,
        onClearFilter = {
            onSortOptionSelect(com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT)
            onSortAscendingToggle(false)
            onFilterSelect("All")
        },
        onDismissRequest = { showSortSheet = false }
    )
}
