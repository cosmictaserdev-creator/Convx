package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacter (dualtone)
 * Viewport: 15.5469 x 18.623
 */
public val SfSymbols.Dualtone.SFCharacter: ImageVector
    get() {
        if (_sFCharacter != null) {
            return _sFCharacter!!
        }
        _sFCharacter = sfIcon(
            name = "Dualtone.SFCharacter",
            viewportWidth = 15.5469f,
            viewportHeight = 18.623f
        ) {
            addSfPath("M0.908203 18.5938C1.45508 18.5938 1.73828 18.3789 1.94336 17.793L7.54883 2.42188L7.61719 2.42188L13.2227 17.793C13.4277 18.3789 13.7109 18.5938 14.2578 18.5938C14.8242 18.5938 15.1855 18.2617 15.1855 17.7148C15.1855 17.5391 15.1465 17.3633 15.0684 17.1387L8.93555 0.976562C8.69141 0.332031 8.26172 0 7.58789 0C6.92383 0 6.48438 0.322266 6.25977 0.966797L0.117188 17.1484C0.0390625 17.373 0 17.5488 0 17.7246C0 18.2715 0.341797 18.5938 0.908203 18.5938ZM3.52539 12.9395L11.6602 12.9395C12.0996 12.9395 12.4609 12.5879 12.4609 12.1387C12.4609 11.6895 12.0996 11.3379 11.6602 11.3379L3.52539 11.3379C3.08594 11.3379 2.72461 11.6895 2.72461 12.1387C2.72461 12.5879 3.08594 12.9395 3.52539 12.9395Z", fillAlpha = 0.85f)
        }
        return _sFCharacter!!
    }

private var _sFCharacter: ImageVector? = null
