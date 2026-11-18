package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeadphones (dualtone)
 * Viewport: 25.6934 x 26.3379
 */
public val SfSymbols.Dualtone.SFHeadphones: ImageVector
    get() {
        if (_sFHeadphones != null) {
            return _sFHeadphones!!
        }
        _sFHeadphones = sfIcon(
            name = "Dualtone.SFHeadphones",
            viewportWidth = 25.6934f,
            viewportHeight = 26.3379f
        ) {
            addSfPath("M0 13.6328C0 17.4121 1.09375 21.5625 2.92969 24.7852C3.19336 25.2441 3.70117 25.3711 4.17969 25.1074C4.63867 24.8535 4.75586 24.3555 4.47266 23.8477C2.77344 20.752 1.81641 17.0508 1.81641 13.6328C1.81641 6.55273 6.16211 1.81641 12.666 1.81641C19.1602 1.81641 23.5156 6.55273 23.5156 13.6328C23.5156 17.0508 22.5488 20.752 20.8496 23.8477C20.5664 24.3555 20.6934 24.8535 21.1426 25.1074C21.6211 25.3711 22.1289 25.2441 22.3926 24.7852C24.2285 21.5625 25.332 17.4121 25.332 13.6328C25.332 5.42969 20.293 0 12.666 0C5.0293 0 0 5.42969 0 13.6328ZM3.67188 24.4629C4.08203 25.8203 5.22461 26.4355 6.60156 26.0352C7.95898 25.6348 8.59375 24.4629 8.18359 23.0957L6.34766 16.9629C5.9375 15.6055 4.80469 14.9902 3.41797 15.3906C2.06055 15.791 1.42578 16.9531 1.83594 18.3203ZM21.6504 24.4629L23.4863 18.3203C23.8965 16.9434 23.2617 15.791 21.9043 15.3906C20.5273 14.9902 19.3848 15.6055 18.9844 16.9629L17.1387 23.0957C16.7285 24.4727 17.3633 25.6348 18.7207 26.0352C20.1074 26.4355 21.2402 25.8203 21.6504 24.4629Z", fillAlpha = 0.85f)
        }
        return _sFHeadphones!!
    }

private var _sFHeadphones: ImageVector? = null
