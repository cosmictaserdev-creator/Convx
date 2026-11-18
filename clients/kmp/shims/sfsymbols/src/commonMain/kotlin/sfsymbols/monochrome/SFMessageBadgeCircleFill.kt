package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageBadgeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMessageBadgeCircleFill: ImageVector
    get() {
        if (_sFMessageBadgeCircleFill != null) {
            return _sFMessageBadgeCircleFill!!
        }
        _sFMessageBadgeCircleFill = sfIcon(
            name = "Monochrome.SFMessageBadgeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM5.54688 12.7344C5.54688 15.2344 6.92383 15.8887 6.92383 17.041C6.92383 17.5293 6.74805 17.8613 6.37695 18.1934C6.13281 18.4082 6.25 18.6914 6.61133 18.6914C7.45117 18.6914 8.34961 18.3789 8.98438 17.9004C10.0586 18.4277 11.3184 18.6914 12.7051 18.6914C16.8652 18.6914 19.873 16.1914 19.873 12.7344C19.873 12.3412 19.8344 11.9606 19.7547 11.5968C19.1953 12.0235 18.5004 12.2754 17.7539 12.2754C15.9082 12.2754 14.375 10.7617 14.375 8.89648C14.375 8.20958 14.5851 7.57085 14.9444 7.03941C14.2489 6.86779 13.4979 6.77734 12.7051 6.77734C8.55469 6.77734 5.54688 9.26758 5.54688 12.7344ZM15.332 8.89648C15.332 10.2246 16.4355 11.3184 17.7539 11.3184C19.0625 11.3184 20.1562 10.2246 20.1562 8.89648C20.1562 7.56836 19.0625 6.49414 17.7539 6.49414C16.4355 6.49414 15.332 7.56836 15.332 8.89648Z", fillAlpha = 0.85f)
        }
        return _sFMessageBadgeCircleFill!!
    }

private var _sFMessageBadgeCircleFill: ImageVector? = null
