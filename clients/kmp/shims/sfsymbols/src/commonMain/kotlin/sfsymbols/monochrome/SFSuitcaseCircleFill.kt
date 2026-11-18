package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitcaseCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSuitcaseCircleFill: ImageVector
    get() {
        if (_sFSuitcaseCircleFill != null) {
            return _sFSuitcaseCircleFill!!
        }
        _sFSuitcaseCircleFill = sfIcon(
            name = "Monochrome.SFSuitcaseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.23633 8.21289C5.97656 8.21289 5.35156 8.81836 5.35156 10.0781L5.35156 16.4258C5.35156 17.666 5.97656 18.291 7.23633 18.291L7.68555 18.291L7.68555 8.21289ZM11.0938 5.83984C10.0391 5.83984 9.3457 6.39648 9.3457 7.54883L9.3457 8.21289L8.69141 8.21289L8.69141 18.291L16.7578 18.291L16.7578 8.21289L16.084 8.21289L16.084 7.54883C16.084 6.39648 15.4492 5.83984 14.3359 5.83984ZM17.7539 8.21289L17.7539 18.291L18.2031 18.291C19.4629 18.291 20.0879 17.666 20.0879 16.4258L20.0879 10.0781C20.0879 8.81836 19.4629 8.21289 18.2031 8.21289ZM15.0195 7.63672L15.0195 8.21289L10.4199 8.21289L10.4199 7.63672C10.4199 7.16797 10.6934 6.9043 11.1719 6.9043L14.2578 6.9043C14.7363 6.9043 15.0195 7.16797 15.0195 7.63672Z", fillAlpha = 0.85f)
        }
        return _sFSuitcaseCircleFill!!
    }

private var _sFSuitcaseCircleFill: ImageVector? = null
