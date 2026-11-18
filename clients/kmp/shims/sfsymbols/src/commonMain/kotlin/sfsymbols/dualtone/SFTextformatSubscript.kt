package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextformatSubscript (dualtone)
 * Viewport: 22.5098 x 24.6582
 */
public val SfSymbols.Dualtone.SFTextformatSubscript: ImageVector
    get() {
        if (_sFTextformatSubscript != null) {
            return _sFTextformatSubscript!!
        }
        _sFTextformatSubscript = sfIcon(
            name = "Dualtone.SFTextformatSubscript",
            viewportWidth = 22.5098f,
            viewportHeight = 24.6582f
        ) {
            addSfPath("M0.908203 21.6113C1.45508 21.6113 1.73828 21.3965 1.94336 20.8105L7.54883 5.43945L7.61719 5.43945L13.2227 20.8105C13.4277 21.3965 13.7109 21.6113 14.2578 21.6113C14.8242 21.6113 15.1855 21.2793 15.1855 20.7324C15.1855 20.5566 15.1465 20.3809 15.0684 20.1562L8.93555 3.99414C8.69141 3.34961 8.26172 3.01758 7.58789 3.01758C6.92383 3.01758 6.48438 3.33984 6.25977 3.98438L0.117188 20.166C0.0390625 20.3906 0 20.5664 0 20.7422C0 21.2891 0.341797 21.6113 0.908203 21.6113ZM3.52539 15.957L11.6602 15.957C12.0996 15.957 12.4609 15.6055 12.4609 15.1562C12.4609 14.707 12.0996 14.3555 11.6602 14.3555L3.52539 14.3555C3.08594 14.3555 2.72461 14.707 2.72461 15.1562C2.72461 15.6055 3.08594 15.957 3.52539 15.957Z", fillAlpha = 0.425f)
            addSfPath("M21.3574 24.6582C21.8359 24.6582 22.1484 24.3457 22.1484 23.8867L22.1484 15.1855C22.1484 14.6582 21.8164 14.3262 21.2891 14.3262C20.8105 14.3262 20.5762 14.502 20.2441 14.7266L18.1152 16.1621C17.8125 16.377 17.6855 16.5625 17.6855 16.8066C17.6855 17.1582 17.9492 17.4414 18.3008 17.4414C18.5059 17.4414 18.6523 17.373 18.8574 17.2266L20.5371 16.123L20.5664 16.123L20.5664 23.8867C20.5664 24.3457 20.8887 24.6582 21.3574 24.6582Z", fillAlpha = 0.85f)
        }
        return _sFTextformatSubscript!!
    }

private var _sFTextformatSubscript: ImageVector? = null
