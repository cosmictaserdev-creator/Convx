package com.composables.sfsymbols

/**
 * Entry point for SF Symbols icon collection for Jetpack Compose.
 *
 * Usage:
 * ```kotlin
 * Icon(
 *     imageVector = SfSymbols.Dualtone.SFHeartFill,
 *     contentDescription = "Favorite",
 *     tint = MaterialTheme.colorScheme.primary
 * )
 * ```
 */
public object SfSymbols {
    /** Dualtone variant with layered opacities for depth (Default) */
    public object Dualtone

    /** Monochrome variant with single uniform fill */
    public object Monochrome
}
