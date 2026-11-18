package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSprinklerFill (dualtone)
 * Viewport: 25.918 x 19.9316
 */
public val SfSymbols.Dualtone.SFSprinklerFill: ImageVector
    get() {
        if (_sFSprinklerFill != null) {
            return _sFSprinklerFill!!
        }
        _sFSprinklerFill = sfIcon(
            name = "Dualtone.SFSprinklerFill",
            viewportWidth = 25.918f,
            viewportHeight = 19.9316f
        ) {
            addSfPath("M9.98047 5.04883L15.5664 5.04883C16.4355 5.04883 17.0215 4.46289 17.0215 3.60352L17.0215 2.49023C17.0215 1.01562 15.9668 0.00976562 14.4043 0.00976562L11.1426 0.00976562C9.58984 0.00976562 8.53516 1.01562 8.53516 2.49023L8.53516 3.60352C8.53516 4.46289 9.12109 5.04883 9.98047 5.04883ZM10.9668 7.89062L14.5996 7.89062L14.5996 4.4043L10.9668 4.4043ZM9.99023 12.1875L15.5664 12.1875C16.4355 12.1875 17.0215 11.6016 17.0215 10.7422L17.0215 8.49609C17.0215 7.63672 16.4355 7.05078 15.5664 7.05078L9.99023 7.05078C9.13086 7.05078 8.54492 7.63672 8.54492 8.49609L8.54492 10.7422C8.54492 11.6016 9.13086 12.1875 9.99023 12.1875ZM10.9668 18.8867L14.5996 18.8867L14.5996 11.5137L10.9668 11.5137ZM0 19.0625C0 19.541 0.390625 19.9316 0.869141 19.9316L24.6973 19.9316C25.166 19.9316 25.5566 19.541 25.5566 19.0625C25.5566 18.5938 25.166 18.2031 24.6973 18.2031L0.869141 18.2031C0.390625 18.2031 0 18.5938 0 19.0625Z", fillAlpha = 0.85f)
        }
        return _sFSprinklerFill!!
    }

private var _sFSprinklerFill: ImageVector? = null
