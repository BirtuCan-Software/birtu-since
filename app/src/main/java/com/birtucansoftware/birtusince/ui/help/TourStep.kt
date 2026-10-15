package com.birtucansoftware.birtusince.ui.help

import androidx.annotation.StringRes

enum class TourHighlightTarget {
    LOGO_APP_NAME,
    HELP_BUTTON,
    SEARCH_BAR,
    ADD_BUTTON,
    STATS_TAB
}

data class GuidedTourStep(
    val stepIndex: Int,
    val target: TourHighlightTarget,
    @param:StringRes val titleRes: Int,
    @param:StringRes val bodyRes: Int
)
