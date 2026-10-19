package com.birtucansoftware.birtusince.domain

/**
 * Filter selection criteria for Home Screen records.
 * Supports inclusion and exclusion for categories and status.
 */
data class FilterSelection(
    val includedCategories: Set<String> = emptySet(),
    val excludedCategories: Set<String> = emptySet(),
    val isLateIncluded: Boolean = false,
    val isLateExcluded: Boolean = false
) {
    val isEmpty: Boolean
        get() = includedCategories.isEmpty() &&
            excludedCategories.isEmpty() &&
            !isLateIncluded &&
            !isLateExcluded

    fun isCategoryIncluded(category: String): Boolean =
        includedCategories.any { it.trim().equals(category.trim(), ignoreCase = true) }

    fun isCategoryExcluded(category: String): Boolean =
        excludedCategories.any { it.trim().equals(category.trim(), ignoreCase = true) }

    fun toggleIncludeCategory(category: String): FilterSelection {
        val trimmed = category.trim()
        return if (isCategoryIncluded(trimmed)) {
            // Already included -> toggle off to neutral
            copy(
                includedCategories = includedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) }.toSet()
            )
        } else {
            // Include category, remove from excluded if present
            copy(
                includedCategories = (includedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) } + trimmed).toSet(),
                excludedCategories = excludedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) }.toSet()
            )
        }
    }

    fun toggleExcludeCategory(category: String): FilterSelection {
        val trimmed = category.trim()
        return if (isCategoryExcluded(trimmed)) {
            // Already excluded -> toggle off to neutral
            copy(
                excludedCategories = excludedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) }.toSet()
            )
        } else {
            // Exclude category, remove from included if present
            copy(
                excludedCategories = (excludedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) } + trimmed).toSet(),
                includedCategories = includedCategories.filterNot { it.trim().equals(trimmed, ignoreCase = true) }.toSet()
            )
        }
    }

    fun toggleIncludeLate(): FilterSelection {
        return if (isLateIncluded) {
            copy(isLateIncluded = false)
        } else {
            copy(isLateIncluded = true, isLateExcluded = false)
        }
    }

    fun toggleExcludeLate(): FilterSelection {
        return if (isLateExcluded) {
            copy(isLateExcluded = false)
        } else {
            copy(isLateExcluded = true, isLateIncluded = false)
        }
    }

    /**
     * Checks if a record with the given category and late status satisfies this filter.
     */
    fun matches(category: String, isLate: Boolean): Boolean {
        if (isEmpty) return true

        // Late filter checks
        if (isLateIncluded && !isLate) return false
        if (isLateExcluded && isLate) return false

        // Excluded categories check
        if (isCategoryExcluded(category)) return false

        // Included categories check (if any included, category must be in the set)
        if (includedCategories.isNotEmpty()) {
            if (!isCategoryIncluded(category)) return false
        }

        return true
    }

    companion object {
        fun fromLegacyString(legacy: String?): FilterSelection {
            if (legacy.isNullOrBlank() || legacy.equals("All", ignoreCase = true)) {
                return FilterSelection()
            }
            if (legacy.equals("Late", ignoreCase = true)) {
                return FilterSelection(isLateIncluded = true)
            }
            return FilterSelection(includedCategories = setOf(legacy.trim()))
        }
    }
}
