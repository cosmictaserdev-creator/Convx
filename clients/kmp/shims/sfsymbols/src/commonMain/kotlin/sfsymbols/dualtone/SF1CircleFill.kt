package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF1CircleFill: ImageVector
    get() {
        if (_sF1CircleFill != null) {
            return _sF1CircleFill!!
        }
        _sF1CircleFill = sfIcon(
            name = "Dualtone.SF1CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M13.3691 18.8086C12.8223 18.8086 12.4902 18.4375 12.4902 17.8125L12.4902 8.60352L12.4121 8.60352L10.5371 9.83398C10.293 9.99023 10.1758 10.0195 10 10.0195C9.63867 10.0195 9.35547 9.69727 9.35547 9.3457C9.35547 9.05273 9.46289 8.87695 9.70703 8.70117L12.1289 7.08008C12.6074 6.74805 12.9004 6.64062 13.3203 6.64062C13.916 6.64062 14.2773 7.00195 14.2773 7.63672L14.2773 17.8125C14.2773 18.4375 13.9453 18.8086 13.3691 18.8086Z", fillAlpha = 0.85f)
        }
        return _sF1CircleFill!!
    }

private var _sF1CircleFill: ImageVector? = null
