package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCubeFill (dualtone)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Dualtone.SFCubeFill: ImageVector
    get() {
        if (_sFCubeFill != null) {
            return _sFCubeFill!!
        }
        _sFCubeFill = sfIcon(
            name = "Dualtone.SFCubeFill",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M12.4414 13.2251L24.2188 6.57471C24.0234 6.35986 23.7988 6.15479 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C1.08398 6.15479 0.849609 6.35986 0.654297 6.58447ZM11.5137 14.856L0.00976562 8.38135C0 8.43018 0 8.49854 0 8.54736L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067L11.0938 27.5513C11.2305 27.6294 11.3672 27.6978 11.5137 27.7466ZM13.3691 14.856L13.3691 27.7466C13.5059 27.6978 13.6426 27.6392 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.5376C24.873 8.48877 24.873 8.42041 24.873 8.37158Z", fillAlpha = 0.85f)
        }
        return _sFCubeFill!!
    }

private var _sFCubeFill: ImageVector? = null
