package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTimerCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTimerCircleFill: ImageVector
    get() {
        if (_sFTimerCircleFill != null) {
            return _sFTimerCircleFill!!
        }
        _sFTimerCircleFill = sfIcon(
            name = "Monochrome.SFTimerCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.9727 6.17188L11.9727 8.25195C11.9727 8.64258 12.2461 8.96484 12.6562 8.96484C13.0566 8.96484 13.3398 8.64258 13.3398 8.25195L13.3398 6.94336C16.2988 7.24609 18.5254 9.66797 18.5254 12.7246C18.5254 15.9766 15.9766 18.5449 12.7148 18.5449C9.46289 18.5449 6.89453 15.9766 6.89453 12.7246C6.89453 11.2207 7.45117 9.92188 8.35938 8.88672C8.61328 8.57422 8.64258 8.13477 8.33984 7.85156C8.04688 7.5293 7.55859 7.56836 7.24609 7.92969C6.11328 9.19922 5.41016 10.8984 5.41016 12.7246C5.41016 16.7188 8.71094 20.0195 12.7148 20.0195C16.7188 20.0195 20.0195 16.7188 20.0195 12.7246C20.0195 8.7207 16.7188 5.41992 12.7246 5.41992C12.2266 5.41992 11.9727 5.73242 11.9727 6.17188ZM9.58984 10.0879L11.9336 13.5156C12.4121 14.2285 13.2715 14.3652 13.877 13.7598C14.4824 13.1348 14.3555 12.2949 13.6523 11.8066L10.2344 9.45312C9.75586 9.15039 9.25781 9.61914 9.58984 10.0879Z", fillAlpha = 0.85f)
        }
        return _sFTimerCircleFill!!
    }

private var _sFTimerCircleFill: ImageVector? = null
