package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTimerCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTimerCircleFill: ImageVector
    get() {
        if (_sFTimerCircleFill != null) {
            return _sFTimerCircleFill!!
        }
        _sFTimerCircleFill = sfIcon(
            name = "Dualtone.SFTimerCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 20.0195C8.71094 20.0195 5.41016 16.7188 5.41016 12.7246C5.41016 10.8984 6.11328 9.19922 7.24609 7.92969C7.55859 7.56836 8.04688 7.5293 8.33984 7.85156C8.64258 8.13477 8.61328 8.57422 8.35938 8.88672C7.45117 9.92188 6.89453 11.2207 6.89453 12.7246C6.89453 15.9766 9.46289 18.5449 12.7148 18.5449C15.9766 18.5449 18.5254 15.9766 18.5254 12.7246C18.5254 9.66797 16.2988 7.24609 13.3398 6.94336L13.3398 8.25195C13.3398 8.64258 13.0566 8.96484 12.6562 8.96484C12.2461 8.96484 11.9727 8.64258 11.9727 8.25195L11.9727 6.17188C11.9727 5.73242 12.2266 5.41992 12.7246 5.41992C16.7188 5.41992 20.0195 8.7207 20.0195 12.7246C20.0195 16.7188 16.7188 20.0195 12.7148 20.0195ZM13.877 13.7598C13.2715 14.3652 12.4121 14.2285 11.9336 13.5156L9.58984 10.0879C9.25781 9.61914 9.75586 9.15039 10.2344 9.45312L13.6523 11.8066C14.3555 12.2949 14.4824 13.1348 13.877 13.7598Z", fillAlpha = 0.85f)
        }
        return _sFTimerCircleFill!!
    }

private var _sFTimerCircleFill: ImageVector? = null
