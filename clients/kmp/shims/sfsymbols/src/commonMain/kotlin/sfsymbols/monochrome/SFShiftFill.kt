package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShiftFill (monochrome)
 * Viewport: 27.373 x 23.3887
 */
public val SfSymbols.Monochrome.SFShiftFill: ImageVector
    get() {
        if (_sFShiftFill != null) {
            return _sFShiftFill!!
        }
        _sFShiftFill = sfIcon(
            name = "Monochrome.SFShiftFill",
            viewportWidth = 27.373f,
            viewportHeight = 23.3887f
        ) {
            addSfPath("M1.63086 14.834L25.3809 14.834C26.3086 14.834 27.0117 14.2773 27.0117 13.418C27.0117 12.8809 26.748 12.4805 26.3281 12.0605L15.0098 0.751953C14.4824 0.234375 14.0234 0 13.5059 0C12.9883 0 12.5293 0.234375 12.0117 0.751953L0.683594 12.0605C0.244141 12.5098 0 12.8809 0 13.418C0 14.2773 0.712891 14.834 1.63086 14.834ZM20.0488 20.6934L20.0488 10.6445C20.0488 9.11133 19.1602 8.14453 17.5488 8.14453L9.46289 8.14453C7.86133 8.14453 6.96289 9.11133 6.96289 10.6445L6.96289 20.6934C6.96289 22.2266 7.86133 23.1836 9.46289 23.1836L17.5488 23.1836C19.1602 23.1836 20.0488 22.2266 20.0488 20.6934Z", fillAlpha = 0.85f)
        }
        return _sFShiftFill!!
    }

private var _sFShiftFill: ImageVector? = null
