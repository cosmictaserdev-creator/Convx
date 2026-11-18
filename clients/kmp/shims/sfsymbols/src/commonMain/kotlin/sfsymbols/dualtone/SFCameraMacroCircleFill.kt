package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCameraMacroCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCameraMacroCircleFill: ImageVector
    get() {
        if (_sFCameraMacroCircleFill != null) {
            return _sFCameraMacroCircleFill!!
        }
        _sFCameraMacroCircleFill = sfIcon(
            name = "Dualtone.SFCameraMacroCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.08789 15.5957C4.96094 15.2344 5.09766 14.9707 5.50781 14.9707C8.11523 14.9707 10.2441 16.2012 11.4941 18.0078L11.4941 20.2832C11.4941 20.4102 11.5234 20.5469 11.5723 20.6543C8.59375 20.2734 6.00586 18.291 5.08789 15.5957ZM8.27148 8.89648C8.27148 8.1543 8.44727 7.41211 8.7793 6.65039C8.88672 6.35742 9.25781 6.30859 9.48242 6.5625L11.0352 8.14453L12.3633 6.42578C12.5586 6.16211 12.8809 6.16211 13.0566 6.42578L14.3848 8.14453L15.9375 6.5625C16.1621 6.30859 16.5234 6.35742 16.6504 6.65039C16.9824 7.41211 17.1387 8.1543 17.1387 8.89648C17.1387 11.2207 15.6055 13.0957 13.2129 13.3301L13.2129 19.1309C14.3262 16.7285 16.7578 14.9707 19.9023 14.9707C20.3027 14.9707 20.4492 15.2344 20.3125 15.5957C19.2676 18.6328 16.123 20.7617 12.7051 20.7617C12.4316 20.7617 12.2168 20.5566 12.2168 20.2832L12.2168 13.3301C9.82422 13.1055 8.27148 11.2207 8.27148 8.89648Z", fillAlpha = 0.85f)
        }
        return _sFCameraMacroCircleFill!!
    }

private var _sFCameraMacroCircleFill: ImageVector? = null
