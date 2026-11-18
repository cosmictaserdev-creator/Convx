package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStop (dualtone)
 * Viewport: 20.5957 x 20.2637
 */
public val SfSymbols.Dualtone.SFStop: ImageVector
    get() {
        if (_sFStop != null) {
            return _sFStop!!
        }
        _sFStop = sfIcon(
            name = "Dualtone.SFStop",
            viewportWidth = 20.5957f,
            viewportHeight = 20.2637f
        ) {
            addSfPath("M0 17.6074C0 19.2676 0.996094 20.2441 2.67578 20.2441L17.5586 20.2441C19.248 20.2441 20.2344 19.2676 20.2344 17.6074L20.2344 2.63672C20.2344 0.976562 19.248 0 17.5586 0L2.67578 0C0.996094 0 0 0.976562 0 2.63672ZM1.72852 17.2168L1.72852 3.02734C1.72852 2.2168 2.20703 1.73828 3.00781 1.73828L17.2363 1.73828C18.0371 1.73828 18.5059 2.2168 18.5059 3.02734L18.5059 17.2168C18.5059 18.0273 18.0371 18.5156 17.2363 18.5156L3.00781 18.5156C2.20703 18.5156 1.72852 18.0273 1.72852 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFStop!!
    }

private var _sFStop: ImageVector? = null
