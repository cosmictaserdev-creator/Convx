package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitcaseCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSuitcaseCircleFill: ImageVector
    get() {
        if (_sFSuitcaseCircleFill != null) {
            return _sFSuitcaseCircleFill!!
        }
        _sFSuitcaseCircleFill = sfIcon(
            name = "Dualtone.SFSuitcaseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.68555 18.291L7.23633 18.291C5.97656 18.291 5.35156 17.666 5.35156 16.4258L5.35156 10.0781C5.35156 8.81836 5.97656 8.21289 7.23633 8.21289L7.68555 8.21289ZM8.69141 18.291L8.69141 8.21289L9.3457 8.21289L9.3457 7.54883C9.3457 6.39648 10.0391 5.83984 11.0938 5.83984L14.3359 5.83984C15.4492 5.83984 16.084 6.39648 16.084 7.54883L16.084 8.21289L16.7578 8.21289L16.7578 18.291ZM10.4199 8.21289L15.0195 8.21289L15.0195 7.63672C15.0195 7.16797 14.7363 6.9043 14.2578 6.9043L11.1719 6.9043C10.6934 6.9043 10.4199 7.16797 10.4199 7.63672ZM17.7539 18.291L17.7539 8.21289L18.2031 8.21289C19.4629 8.21289 20.0879 8.81836 20.0879 10.0781L20.0879 16.4258C20.0879 17.666 19.4629 18.291 18.2031 18.291Z", fillAlpha = 0.85f)
        }
        return _sFSuitcaseCircleFill!!
    }

private var _sFSuitcaseCircleFill: ImageVector? = null
