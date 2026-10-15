package com.birtucansoftware.birtusince

import androidx.lifecycle.SavedStateHandle
import com.birtucansoftware.birtusince.ui.help.HelpTourViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HelpTourViewModelTest {

    @Test
    fun starts_at_step_1_with_maximum_5_steps() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = HelpTourViewModel(savedStateHandle)

        assertEquals(0, viewModel.currentStepIndex.value)
        assertTrue(viewModel.isFirstStep())
        assertFalse(viewModel.isLastStep())
        // Tour must not exceed 5 steps
        assertEquals(5, viewModel.tourSteps.size)
    }

    @Test
    fun next_and_back_move_correctly() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = HelpTourViewModel(savedStateHandle)

        // Step 0 -> Step 1
        viewModel.nextStep()
        assertEquals(1, viewModel.currentStepIndex.value)
        assertFalse(viewModel.isFirstStep())

        // Step 1 -> Step 2
        viewModel.nextStep()
        assertEquals(2, viewModel.currentStepIndex.value)

        // Step 2 -> Step 1
        viewModel.previousStep()
        assertEquals(1, viewModel.currentStepIndex.value)

        // Step 1 -> Step 0
        viewModel.previousStep()
        assertEquals(0, viewModel.currentStepIndex.value)
        assertTrue(viewModel.isFirstStep())
    }

    @Test
    fun back_on_step_1_finishes_tour() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = HelpTourViewModel(savedStateHandle)

        var eventReceived = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.closeTourEvent.collect {
                eventReceived = true
            }
        }

        // At step 0, calling previousStep() emits closeTourEvent
        viewModel.previousStep()
        assertTrue(eventReceived)
    }

    @Test
    fun next_on_last_step_finishes_tour() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(HelpTourViewModel.KEY_CURRENT_STEP to 4))
        val viewModel = HelpTourViewModel(savedStateHandle)

        assertEquals(4, viewModel.currentStepIndex.value)
        assertTrue(viewModel.isLastStep())

        var eventReceived = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.closeTourEvent.collect {
                eventReceived = true
            }
        }

        viewModel.nextStep()
        assertTrue(eventReceived)
    }

    @Test
    fun cancel_finishes_from_any_step() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(HelpTourViewModel.KEY_CURRENT_STEP to 2))
        val viewModel = HelpTourViewModel(savedStateHandle)

        assertEquals(2, viewModel.currentStepIndex.value)

        var eventReceived = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.closeTourEvent.collect {
                eventReceived = true
            }
        }

        viewModel.cancelTour()
        assertTrue(eventReceived)
    }

    @Test
    fun state_is_restored_after_recreation() {
        val savedStateHandle = SavedStateHandle(mapOf(HelpTourViewModel.KEY_CURRENT_STEP to 3))
        val viewModel = HelpTourViewModel(savedStateHandle)

        assertEquals(3, viewModel.currentStepIndex.value)
        assertEquals(3, savedStateHandle.get<Int>(HelpTourViewModel.KEY_CURRENT_STEP))
    }
}
