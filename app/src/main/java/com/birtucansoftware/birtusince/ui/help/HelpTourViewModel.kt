package com.birtucansoftware.birtusince.ui.help

import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.birtucansoftware.birtusince.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class HelpTourViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_CURRENT_STEP = "key_current_tour_step"
    }

    val tourSteps: List<GuidedTourStep> = listOf(
        GuidedTourStep(
            stepIndex = 0,
            target = TourHighlightTarget.LOGO_APP_NAME,
            titleRes = R.string.tour_guided_step1_title,
            bodyRes = R.string.tour_guided_step1_body
        ),
        GuidedTourStep(
            stepIndex = 1,
            target = TourHighlightTarget.HELP_BUTTON,
            titleRes = R.string.tour_guided_step2_title,
            bodyRes = R.string.tour_guided_step2_body
        ),
        GuidedTourStep(
            stepIndex = 2,
            target = TourHighlightTarget.SEARCH_BAR,
            titleRes = R.string.tour_guided_step3_title,
            bodyRes = R.string.tour_guided_step3_body
        ),
        GuidedTourStep(
            stepIndex = 3,
            target = TourHighlightTarget.ADD_BUTTON,
            titleRes = R.string.tour_guided_step4_title,
            bodyRes = R.string.tour_guided_step4_body
        ),
        GuidedTourStep(
            stepIndex = 4,
            target = TourHighlightTarget.STATS_TAB,
            titleRes = R.string.tour_guided_step5_title,
            bodyRes = R.string.tour_guided_step5_body
        )
    )

    private val _currentStepIndex = MutableStateFlow(savedStateHandle.get<Int>(KEY_CURRENT_STEP) ?: 0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _closeTourEvent = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)
    val closeTourEvent: SharedFlow<Unit> = _closeTourEvent.asSharedFlow()

    fun nextStep() {
        val current = _currentStepIndex.value
        if (current < tourSteps.size - 1) {
            val next = current + 1
            _currentStepIndex.value = next
            savedStateHandle[KEY_CURRENT_STEP] = next
        } else {
            _closeTourEvent.tryEmit(Unit)
        }
    }

    fun previousStep() {
        val current = _currentStepIndex.value
        if (current > 0) {
            val prev = current - 1
            _currentStepIndex.value = prev
            savedStateHandle[KEY_CURRENT_STEP] = prev
        } else {
            _closeTourEvent.tryEmit(Unit)
        }
    }

    fun cancelTour() {
        _closeTourEvent.tryEmit(Unit)
    }

    fun resetToStart() {
        _currentStepIndex.value = 0
        savedStateHandle[KEY_CURRENT_STEP] = 0
    }

    fun isFirstStep(): Boolean = _currentStepIndex.value == 0
    fun isLastStep(): Boolean = _currentStepIndex.value == tourSteps.size - 1
}
