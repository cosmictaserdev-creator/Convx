package com.convx.music.catalog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.convx.music.catalog.CatalogId
import com.convx.music.designsystem.MotionTokens

private val LocalCatalogSharedScope = compositionLocalOf<SharedTransitionScope?> { null }
private val LocalCatalogVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/** Wraps catalog routes with the Android navigation choreography and shared artwork scope. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CatalogSharedTransitionPage(
    state: CatalogScreenState,
    modifier: Modifier = Modifier,
    enableSharedElements: Boolean = true,
    animationsEnabled: Boolean = true,
    content: @Composable (CatalogScreenState) -> Unit,
) {
    if (!animationsEnabled) {
        androidx.compose.foundation.layout.Box(modifier) { content(state) }
        return
    }
    if (!enableSharedElements) {
        AnimatedContent(
            targetState = state,
            contentKey = { it.transitionKey() },
            transitionSpec = { catalogRouteTransition(initialState, targetState) },
            label = "CatalogRouteTransition",
        ) { pageState -> content(pageState) }
        return
    }

    SharedTransitionLayout(modifier) {
        val sharedScope = this
        AnimatedContent(
            targetState = state,
            contentKey = { it.transitionKey() },
            transitionSpec = { catalogRouteTransition(initialState, targetState) },
            modifier = Modifier,
            label = "CatalogSharedRouteTransition",
        ) { pageState ->
            CompositionLocalProvider(
                LocalCatalogSharedScope provides sharedScope,
                LocalCatalogVisibilityScope provides this,
            ) {
                content(pageState)
            }
        }
    }
}

private fun catalogRouteTransition(
    initialState: CatalogScreenState,
    targetState: CatalogScreenState,
): ContentTransform = when {
    targetState.currentEntry != null ->
        slideInHorizontally(MotionTokens.artwork()) { it } togetherWith
            fadeOut(MotionTokens.artwork(), targetAlpha = 0.7f)

    initialState.currentEntry != null ->
        androidx.compose.animation.EnterTransition.None togetherWith
            fadeOut(MotionTokens.artwork())

    else ->
        (slideInHorizontally(MotionTokens.push()) { it } + fadeIn(MotionTokens.push())) togetherWith
            (slideOutHorizontally(MotionTokens.push()) { MotionTokens.parallaxOffset(it) } +
                fadeOut(MotionTokens.push(), targetAlpha = MotionTokens.PushDimAlpha))
}

private fun CatalogScreenState.transitionKey(): String = currentEntry?.let { entry ->
    "entry:${entry.id.providerId}:${entry.id.value}"
} ?: "root:$pageTitle"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedCatalogArtwork(id: CatalogId): Modifier {
    val sharedScope = LocalCatalogSharedScope.current ?: return this
    val visibilityScope = LocalCatalogVisibilityScope.current ?: return this
    return with(sharedScope) {
        this@sharedCatalogArtwork.sharedElement(
            sharedContentState = rememberSharedContentState("catalog-artwork:${id.providerId}:${id.value}"),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> MotionTokens.sharedArtwork() },
            renderInOverlayDuringTransition = true,
            zIndexInOverlay = 1f,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedCatalogTitle(id: CatalogId): Modifier {
    val sharedScope = LocalCatalogSharedScope.current ?: return this
    val visibilityScope = LocalCatalogVisibilityScope.current ?: return this
    return with(sharedScope) {
        this@sharedCatalogTitle.sharedBounds(
            sharedContentState = rememberSharedContentState("catalog-title:${id.providerId}:${id.value}"),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> MotionTokens.artwork() },
            renderInOverlayDuringTransition = true,
            zIndexInOverlay = 2f,
        )
    }
}
