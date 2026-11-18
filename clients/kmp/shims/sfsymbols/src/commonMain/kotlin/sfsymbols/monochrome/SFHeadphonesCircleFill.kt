package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeadphonesCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHeadphonesCircleFill: ImageVector
    get() {
        if (_sFHeadphonesCircleFill != null) {
            return _sFHeadphonesCircleFill!!
        }
        _sFHeadphonesCircleFill = sfIcon(
            name = "Monochrome.SFHeadphonesCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM5.79102 12.7148C5.79102 15.0488 6.35742 16.9727 7.34375 18.6914C7.55859 19.0625 7.93945 19.2285 8.31055 19.1211C8.64258 19.4531 9.13086 19.5801 9.6582 19.4043C10.4785 19.1797 10.8594 18.4766 10.6152 17.666L9.6582 14.4434C9.41406 13.623 8.7207 13.2422 7.89062 13.4863C7.68555 13.5449 7.5 13.6328 7.35352 13.75C7.31445 13.418 7.30469 13.0762 7.30469 12.7148C7.30469 9.16016 9.48242 6.78711 12.7246 6.78711C15.9863 6.78711 18.1641 9.16992 18.1641 12.7148C18.1641 13.0762 18.1445 13.4277 18.1152 13.7695C17.959 13.6426 17.7734 13.5449 17.5488 13.4863C16.7383 13.2422 16.0449 13.623 15.8105 14.4434L14.834 17.6758C14.5996 18.4863 14.9707 19.1797 15.791 19.4043C16.3281 19.5703 16.8066 19.4531 17.1387 19.1211C17.5098 19.2285 17.9004 19.0625 18.1152 18.6914C19.1211 16.9336 19.6875 15.0195 19.6875 12.7148C19.6875 8.25195 16.8945 5.26367 12.7246 5.26367C8.56445 5.26367 5.79102 8.24219 5.79102 12.7148Z", fillAlpha = 0.85f)
        }
        return _sFHeadphonesCircleFill!!
    }

private var _sFHeadphonesCircleFill: ImageVector? = null
