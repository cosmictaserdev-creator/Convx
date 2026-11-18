package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFJCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFJCircleFill: ImageVector
    get() {
        if (_sFJCircleFill != null) {
            return _sFJCircleFill!!
        }
        _sFJCircleFill = sfIcon(
            name = "Monochrome.SFJCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM14.082 7.64648L14.082 15C14.082 16.3574 13.3691 17.1777 12.2266 17.1777C11.1816 17.1777 10.4492 16.6211 10.166 15.7031C9.98047 15.2734 9.72656 15.0879 9.3457 15.0879C8.84766 15.0879 8.51562 15.4199 8.51562 15.9277C8.51562 16.1133 8.54492 16.2891 8.61328 16.4746C9.01367 17.8125 10.3906 18.7109 12.1387 18.7109C14.541 18.7109 15.8594 17.373 15.8594 15.0195L15.8594 7.64648C15.8594 7.03125 15.5469 6.65039 14.9609 6.65039C14.3945 6.65039 14.082 7.05078 14.082 7.64648Z", fillAlpha = 0.85f)
        }
        return _sFJCircleFill!!
    }

private var _sFJCircleFill: ImageVector? = null
