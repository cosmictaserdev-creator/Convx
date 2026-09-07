@file:OptIn(ExperimentalSharedTransitionApi::class)

/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import android.os.Bundle
import androidx.navigation.NavBackStackEntry
import com.convxy.music.constants.ThumbnailCornerRadius
import com.convxy.music.ui.player.rememberScreenCornerRadius

/**
 * The app-wide [SharedTransitionScope], provided once around the NavHost.
 *
 * Null wherever no scope is in play -- a screen rendered outside the NavHost, a preview,
 * a dialog. [morphContainer] then does nothing rather than crashing, which is why this is
 * nullable instead of `error()`-ing like the app's other required locals: a screen is
 * always drawable, and only sometimes morphable.
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * The current destination's [AnimatedVisibilityScope] — the NavHost entry's own, which is
 * what tells the shared element which transition it is participating in.
 */
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Stable identity for one morph pair: the tapped tile and the screen it opens.
 *
 * A data class rather than the bare id string so it cannot collide with any other shared
 * key in the app -- shared content is matched across the whole SharedTransitionLayout,
 * not per screen, so a plain `"abc123"` would match anything else that happened to use
 * the same string.
 */
data class SharedArtworkKey(val id: String)

/**
 * True while the in-flight navigation is a pop (closing a morph route back to its
 * tile) rather than a push (opening one). Both halves of the shared bounds read this
 * -- it has to be one flag, not derived independently per side, or the two ends can
 * disagree about which curve they are on and visibly shear apart mid-flight.
 *
 * Set from [markMorphDirection], called alongside [markSharedArtworkSource] in
 * MainActivity's `OnDestinationChangedListener`, which already fires on every
 * navigation -- comparing back-stack size before and after that same event is a push
 * vs a pop, deterministically, without threading a signal through every place a morph
 * route's back button or system back is handled.
 */
private val isMorphClosing = mutableStateOf(false)

/** See [isMorphClosing]. */
fun markMorphDirection(isPop: Boolean) {
    isMorphClosing.value = isPop
}

private val MorphBoundsTransform = BoundsTransform { _, _ ->
    if (isMorphClosing.value) Motion.morphExit() else Motion.morphEnter()
}

/**
 * The artwork the user last tapped to navigate.
 *
 * Home shows the same album in more than one rail all the time -- quick picks and
 * "similar to", say. Shared elements are matched BY KEY, so if every tile claimed its
 * id, two live elements would answer to one key in the same frame and the detail
 * screen would morph out of whichever the framework happened to pick. That is the
 * "artwork flew in from the wrong tile" bug, and it survives the move from
 * sharedElement to sharedBounds unchanged -- the matching rule is the same.
 *
 * So a tile only becomes a shared element once it is the one being opened. The detail
 * screen needs no such guard: there is only ever one of it.
 *
 * Plain state rather than a nav argument because it is a presentation detail -- losing
 * it (process death, a deep link, a programmatic navigate) costs the morph, not
 * correctness.
 */
private val pendingSharedArtworkId = mutableStateOf<String?>(null)

/**
 * Every artwork currently placed on screen, by item id.
 *
 * Plain map, not snapshot state: this is written from the layout pass on every tile on
 * every scroll frame, and nothing composes off it.
 */
private val placedArtwork = HashMap<String, Pair<LayoutCoordinates, String?>>()

/**
 * The item id a destination's route carries, if any.
 *
 * These argument names cover every artwork-bearing route in the app. Keeping the list
 * in one place is what stops the listener that nominates a morph source and the
 * container that consumes it from drifting apart.
 */
fun NavBackStackEntry.morphArtworkId(): String? = arguments.morphArtworkId()

/**
 * Same lookup against a raw argument [Bundle], for `OnDestinationChangedListener` --
 * which is handed arguments, not an entry.
 *
 * Deliberately the same function as the entry overload rather than a second copy of the
 * key list. The listener nominates the source half and the destination's container
 * consumes it; if the two disagree about which argument names carry an id, a route is
 * silently left with a destination that expects a morph and a source that was never
 * nominated, and it falls back to a cross-fade for no visible reason.
 */
fun Bundle?.morphArtworkId(): String? =
    this?.getString("albumId")
        ?: this?.getString("artistId")
        ?: this?.getString("playlistId")
        ?: this?.getString("browseId")
        ?: this?.getString("playlist")
        ?: this?.getString("top")
        ?: this?.getString("path")

/** True for the destinations that open with a container morph -- see `sharedComposable`. */
fun isMorphRoute(route: String?): Boolean =
    route != null && MorphRoutePrefixes.any(route::startsWith)

private val MorphRoutePrefixes =
    listOf(
        "album/",
        "artist/",
        "online_playlist/",
        "local_playlist/",
        "auto_playlist/",
        "cache_playlist/",
        "top_playlist/",
        "local_folder/",
        "youtube_browse/",
        "browse/",
    )

/**
 * Call as navigation to [id]'s detail screen begins: nominates that tile as the source
 * of the morph. Returns the tile's artwork URL, for warming anything the destination
 * will want.
 *
 * A null or unknown id leaves the previous nomination in place rather than clearing it
 * -- popping back off a detail screen reports a destination with no id at all, and
 * clearing here would drop the source half of the morph exactly when the reverse morph
 * needs it.
 */
fun markSharedArtworkSource(id: String?): String? {
    if (id.isNullOrEmpty()) return null
    pendingSharedArtworkId.value = id
    return placedArtwork[id]?.second
}

/**
 * Whether [id] has a live [sharedArtworkSource] tile registered right now.
 *
 * Not the same question as "is [id] non-null" -- a route can carry a perfectly good
 * id while the tile that opened it never called [sharedArtworkSource] at all (a
 * bespoke card that renders its own `AsyncImage` instead of going through one of
 * Items.kt's shared composables). That case has a real id, so [morphContainer] would
 * still try to claim the shared-bounds key, find no partner, and the destination
 * would render with none of the enter animation applied anywhere -- not the real
 * morph (no partner to interpolate from) and not the generic fallback either (which
 * used to gate on id-nullness alone). This is the check that closes that gap: a
 * `sharedComposable` should fall back to [Modifier.animateEnterExit] whenever this
 * is false, id or no id.
 */
fun hasMorphSource(id: String): Boolean = placedArtwork.containsKey(id)

/**
 * Source half of the morph: the tile's artwork box.
 *
 * Always records where this artwork is, so anything that wants the tile's thumbnail
 * URL can read it without every tile in the app routing its click through a shared
 * helper. Only becomes an actual shared element once this tile is the one being
 * opened -- see [pendingSharedArtworkId].
 *
 * Deliberately a `sharedBounds`, not a `sharedElement`, and deliberately on the
 * ARTWORK box rather than the whole tile card. Measured off the reference: the
 * shrinking card lands exactly on the artwork square, with the tile's text label
 * below it never part of the morph.
 */
@Composable
fun Modifier.sharedArtworkSource(id: String?, thumbnailUrl: String? = null): Modifier {
    if (id.isNullOrEmpty()) return this

    DisposableEffect(id) { onDispose { placedArtwork.remove(id) } }
    val track = remember(id, thumbnailUrl) {
        Modifier.onPlaced { placedArtwork[id] = it to thumbnailUrl }
    }

    return if (pendingSharedArtworkId.value != id) {
        then(track)
    } else {
        then(track).morphContainer(id, isSource = true)
    }
}

/**
 * One end of the container morph.
 *
 * On the tile it is the artwork box; on the detail screen it is the whole screen root.
 * Between them Compose lifts both into the shared overlay and interpolates one rect
 * into the other, scaling the detail screen's already-laid-out content down into the
 * tile's square and cropping the overflow -- which is what the reference does. The
 * corner radius travels with it, from the screen's own radius to the tile's.
 *
 * Content scales into the card rather than re-measuring: the reference clearly
 * scales finished layout (text, buttons and tracklist all shrink proportionally inside
 * the card) instead of re-laying-out the detail screen at tile width every frame, which
 * would also be a full measure/layout pass per frame of the transition.
 *
 * Silently returns the receiver when either scope is missing, so a call site is safe to
 * add before its screen has been wired into the NavHost -- it simply cross-fades.
 */
@Composable
fun Modifier.morphContainer(id: String?, isSource: Boolean): Modifier {
    if (id.isNullOrEmpty()) return this
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val animatedScope = LocalNavAnimatedVisibilityScope.current ?: return this

    val screenRadius = rememberScreenCornerRadius()
    // Source and destination must animate the radius through the SAME values or the
    // two halves disagree about the card's shape mid-flight. Both read tile radius at
    // the tile end and screen radius at the screen end; which end each one starts from
    // is the only difference, and EnterExitState already expresses that.
    val radius by animatedScope.transition.animateDp(
        // Same flag as MorphBoundsTransform, so the radius and the rect it clips
        // never fall out of sync mid-flight.
        transitionSpec = { if (isMorphClosing.value) Motion.morphExit() else Motion.morphEnter() },
        label = "morphCornerRadius",
    ) { state ->
        when (state) {
            EnterExitState.Visible -> if (isSource) ThumbnailCornerRadius else screenRadius
            // Both ends of the flight sit on the tile, so both take the tile's radius
            // no matter which half of the morph this is.
            EnterExitState.PreEnter, EnterExitState.PostExit -> ThumbnailCornerRadius
        }
    }

    return with(sharedScope) {
        this@morphContainer
            .sharedBounds(
                sharedContentState = rememberSharedContentState(SharedArtworkKey(id)),
                animatedVisibilityScope = animatedScope,
                boundsTransform = MorphBoundsTransform,
                // The compose-animation API is lowercase -- `scaleToBounds`, not
                // `ScaleToBounds` -- confirmed via javap against the actual
                // animation-android-1.11.2 aar. The capitalized name silently fell
                // back to the DEFAULT resizeMode instead, which is RemeasureToBounds:
                // that forces the whole destination screen (every row of its
                // LazyColumn) through a full measure + layout pass on EVERY frame of
                // the transition. That is what the dropped-frame jank and the
                // content-reflowing-instead-of-scaling "just popping" look both were.
                resizeMode = ResizeMode.scaleToBounds(ContentScale.Crop, Alignment.TopCenter),
                // Weighted to the ends, not spread across the flight. Measured on the
                // reference: through the body of the morph the card is fully opaque and
                // its content simply rides along scaled -- the detail screen's title and
                // buttons are still legible inside a card barely bigger than the tile --
                // and only in the last ~3 frames does that content give way to the tile
                // beneath it. A crossfade spread over the whole flight instead makes the
                // two halves ghost through each other the entire way, which is the single
                // most obvious tell that a morph is two images and not one object.
                //
                // Arriving (push) resolves early so the card is opaque while it is still
                // growing; leaving (pop) holds and drops at the very end. Scaled to the
                // same proportion of the flight as before (roughly the first/last fifth)
                // now that MorphEnterMillis/MorphExitStiffness are slower -- these were
                // still 80ms/90ms after that change, i.e. a sixth of the new flight
                // instead of a fifth, which read as an abrupt little flash at the start
                // of an otherwise slow, smooth motion.
                enter = fadeIn(tween(110, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(120, delayMillis = 120, easing = FastOutSlowInEasing)),
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(radius)),
            )
            .clip(RoundedCornerShape(radius))
    }
}
