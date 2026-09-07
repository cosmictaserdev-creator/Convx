/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.convxy.music.extensions.toEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadOnlyProperty

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Last known full [Preferences] snapshot, kept current by [preferenceMirror].
 *
 * Every seed read below used to be its own `runBlocking(Dispatchers.IO) { data.first() }`:
 * a main-thread block, a dispatcher hop, and a deserialisation of the WHOLE preferences
 * file, to pull out one key. That is per call site, not per key — a screen reading
 * fourteen preferences paid it fourteen times on the frame it first composed, and again
 * for any call site that first composes later (a lazy item scrolling into view). With
 * ~200 call sites across the app it is a steady background tax on every screen entry.
 *
 * One mirror serves all of them. After the first read the cost is a map lookup.
 */
private val preferenceSnapshot = AtomicReference<Preferences?>(null)

private val preferenceMirrorStarted = AtomicBoolean(false)

/**
 * Keeps [preferenceSnapshot] in step with the store. Started on first access and never
 * cancelled — it is process-wide state backing a process-wide store, so there is no
 * scope narrower than the process that would be correct to tie it to.
 */
private fun DataStore<Preferences>.startPreferenceMirror() {
    if (!preferenceMirrorStarted.compareAndSet(false, true)) return
    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
        data.collect { preferenceSnapshot.set(it) }
    }
}

/**
 * The current snapshot, blocking only if nothing has been read yet.
 *
 * The mirror lags a write by however long DataStore takes to emit it. That window does
 * not affect the UI: writes go through the [MutableState] returned by
 * [rememberPreference], which updates its own value immediately, and the Flow
 * collectors there observe the store directly rather than through this cache. This is
 * only ever the *seed* for a call site's first frame.
 */
private fun DataStore<Preferences>.snapshot(): Preferences {
    startPreferenceMirror()
    preferenceSnapshot.get()?.let { return it }
    return runBlocking(Dispatchers.IO) { data.first() }
        .also { preferenceSnapshot.set(it) }
}

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? =
    snapshot()[key]

fun <T> DataStore<Preferences>.get(
    key: Preferences.Key<T>,
    defaultValue: T,
): T = snapshot()[key] ?: defaultValue

fun <T> preference(
    context: Context,
    key: Preferences.Key<T>,
    defaultValue: T,
) = ReadOnlyProperty<Any?, T> { _, _ -> context.dataStore[key] ?: defaultValue }

inline fun <reified T : Enum<T>> enumPreference(
    context: Context,
    key: Preferences.Key<String>,
    defaultValue: T,
) = ReadOnlyProperty<Any?, T> { _, _ -> context.dataStore[key].toEnum(defaultValue) }

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // The seed value goes through DataStore's blocking accessor, which does a
    // runBlocking disk read. As a bare argument it was re-evaluated on every
    // recomposition, so each preference a composable reads cost one synchronous
    // file read per recomposition — on the main thread. remember pins it to the
    // first composition, which is the only one whose value is ever used.
    val initialValue = remember { context.dataStore[key] ?: defaultValue }
    val state =
        remember {
            context.dataStore.data
                .map { it[key] ?: defaultValue }
                .distinctUntilChanged()
        }.collectAsState(initialValue)

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value

            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
inline fun <reified T : Enum<T>> rememberEnumPreference(
    key: Preferences.Key<String>,
    defaultValue: T,
): MutableState<T> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // remembered for the same reason as in [rememberPreference]: this is a
    // blocking main-thread disk read, and unremembered it ran on every
    // recomposition.
    val initialValue = remember { context.dataStore[key].toEnum(defaultValue = defaultValue) }
    val state =
        remember {
            context.dataStore.data
                .map { it[key].toEnum(defaultValue = defaultValue) }
                .distinctUntilChanged()
        }.collectAsState(initialValue)

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value.name
                        }
                    }
                }

            override fun component1() = value

            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}
