package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArchiveboxFill (monochrome)
 * Viewport: 25.332 x 22.9785
 */
public val SfSymbols.Monochrome.SFArchiveboxFill: ImageVector
    get() {
        if (_sFArchiveboxFill != null) {
            return _sFArchiveboxFill!!
        }
        _sFArchiveboxFill = sfIcon(
            name = "Monochrome.SFArchiveboxFill",
            viewportWidth = 25.332f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M5.51758 22.9785L19.4434 22.9785C21.8457 22.9785 23.1348 21.7285 23.1348 19.3359L23.1348 7.13867L1.83594 7.13867L1.83594 19.3359C1.83594 21.7383 3.125 22.9785 5.51758 22.9785ZM8.27148 11.5527C7.77344 11.5527 7.44141 11.2109 7.44141 10.6934L7.44141 10.3125C7.44141 9.79492 7.77344 9.46289 8.27148 9.46289L16.6992 9.46289C17.207 9.46289 17.5391 9.79492 17.5391 10.3125L17.5391 10.6934C17.5391 11.2109 17.207 11.5527 16.6992 11.5527ZM2.04102 5.57617L22.9199 5.57617C24.2676 5.57617 24.9707 4.76562 24.9707 3.42773L24.9707 2.17773C24.9707 0.839844 24.2676 0.0292969 22.9199 0.0292969L2.04102 0.0292969C0.761719 0.0292969 0 0.839844 0 2.17773L0 3.42773C0 4.76562 0.703125 5.57617 2.04102 5.57617Z", fillAlpha = 0.85f)
        }
        return _sFArchiveboxFill!!
    }

private var _sFArchiveboxFill: ImageVector? = null
