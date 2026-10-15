package com.birtucansoftware.birtusince.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Shared once-per-second tick aligned to the second.
 * Runs ONLY while the screen is in Lifecycle.State.RESUMED (UI Spec 7.2 & PRS NFR-PERF-003).
 * Stops completely when the app is in the background.
 */
@Composable
fun rememberCurrentTick(): State<Long> {
    val tickState = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (isActive) {
                val now = System.currentTimeMillis()
                tickState.longValue = now
                // Align to the next wall-clock second boundary
                val sleepMillis = 1000L - (now % 1000L)
                delay(sleepMillis.coerceAtLeast(100L))
            }
        }
    }

    return tickState
}
