package com.takaotech.dashboard.ui.utils

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// https://stackoverflow.com/questions/54827455/how-to-implement-timer-with-kotlin-coroutines
fun tickerFlow(
    period: Duration,
    initialDelay: Duration = Duration.ZERO,
) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

/**
 * Ticker counter flow
 *
 * @param period
 * @param initialDelay
 */
fun tickerCounterFlow(
    period: Duration,
    initialDelay: Duration = Duration.ZERO,
) = flow {
    delay(initialDelay)
    for (current in period.inWholeSeconds downTo 0) {
        if (currentCoroutineContext().isActive) {
            emit(current)
            delay(1.seconds)
        } else {
            break
        }
    }
}
