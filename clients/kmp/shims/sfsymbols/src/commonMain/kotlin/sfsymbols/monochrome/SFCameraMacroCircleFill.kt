package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCameraMacroCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCameraMacroCircleFill: ImageVector
    get() {
        if (_sFCameraMacroCircleFill != null) {
            return _sFCameraMacroCircleFill!!
        }
        _sFCameraMacroCircleFill = sfIcon(
            name = "Monochrome.SFCameraMacroCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.3633 6.42578L11.0352 8.14453L9.48242 6.5625C9.25781 6.30859 8.88672 6.35742 8.7793 6.65039C8.44727 7.41211 8.27148 8.1543 8.27148 8.89648C8.27148 11.2207 9.82422 13.1055 12.2168 13.3301L12.2168 20.2832C12.2168 20.5566 12.4316 20.7617 12.7051 20.7617C16.123 20.7617 19.2676 18.6328 20.3125 15.5957C20.4492 15.2344 20.3027 14.9707 19.9023 14.9707C16.7578 14.9707 14.3262 16.7285 13.2129 19.1309L13.2129 13.3301C15.6055 13.0957 17.1387 11.2207 17.1387 8.89648C17.1387 8.1543 16.9824 7.41211 16.6504 6.65039C16.5234 6.35742 16.1621 6.30859 15.9375 6.5625L14.3848 8.14453L13.0566 6.42578C12.8809 6.16211 12.5586 6.16211 12.3633 6.42578ZM5.08789 15.5957C6.00586 18.291 8.59375 20.2734 11.5723 20.6543C11.5234 20.5469 11.4941 20.4102 11.4941 20.2832L11.4941 18.0078C10.2441 16.2012 8.11523 14.9707 5.50781 14.9707C5.09766 14.9707 4.96094 15.2344 5.08789 15.5957Z", fillAlpha = 0.85f)
        }
        return _sFCameraMacroCircleFill!!
    }

private var _sFCameraMacroCircleFill: ImageVector? = null
