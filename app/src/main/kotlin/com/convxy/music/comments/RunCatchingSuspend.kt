/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import kotlinx.coroutines.CancellationException

/**
 * [runCatching] that does not swallow cancellation.
 *
 * `runCatching` catches *everything*, and "everything" includes the [CancellationException] that
 * structured concurrency uses to stop a coroutine. Wrap a suspend call in it and a cancelled comment
 * fetch keeps running: it holds its socket open, finishes the work nobody is waiting for, and then
 * reports its own cancellation as an ordinary failure — which the caller has no way to distinguish
 * from "SoundCloud is down".
 *
 * Every `runCatching` in this feature wraps a suspend call that can outlive the track it was started
 * for, because skipping a song is exactly what cancels it. So every one of them uses this instead.
 *
 * Not `inline`: the block is a `suspend` lambda called from inside a `try`, and a non-inline function
 * is the version of that which has no restrictions on where the lambda may return to.
 */
internal suspend fun <T> runCatchingSuspend(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        // Put it back. Whoever cancelled this coroutine is the only one allowed to observe it.
        throw cancellation
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
