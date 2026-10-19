package com.birtucansoftware.birtusince.domain

/**
 * Available sorting criteria for the active tracking records.
 */
enum class SortOption(
    val title: String,
    val description: String,
    val descendingLabel: String,
    val ascendingLabel: String
) {
    CREATED_AT(
        title = "Created date",
        description = "When records were first tracked",
        descendingLabel = "Newest first",
        ascendingLabel = "Oldest first"
    ),
    MOST_LAPS(
        title = "Most laps",
        description = "Highest restart count",
        descendingLabel = "Most laps",
        ascendingLabel = "Fewest laps"
    ),
    LAST_MODIFIED(
        title = "Last modified",
        description = "Recently updated or reset",
        descendingLabel = "Recently modified",
        ascendingLabel = "Least recent"
    ),
    LONGEST_ELAPSED(
        title = "Longest elapsed",
        description = "Longest elapsed duration",
        descendingLabel = "Longest first",
        ascendingLabel = "Shortest first"
    );

    companion object {
        fun fromName(name: String?): SortOption =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: CREATED_AT
    }
}
