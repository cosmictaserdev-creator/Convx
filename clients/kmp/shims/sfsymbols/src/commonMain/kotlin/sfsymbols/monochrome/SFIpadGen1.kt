package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpadGen1 (monochrome)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Monochrome.SFIpadGen1: ImageVector
    get() {
        if (_sFIpadGen1 != null) {
            return _sFIpadGen1!!
        }
        _sFIpadGen1 = sfIcon(
            name = "Monochrome.SFIpadGen1",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031L0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998ZM1.72852 23.8965L1.72852 4.10156L18.9844 4.10156L18.9844 23.8965ZM10.3711 26.7969C9.79492 26.7969 9.32617 26.3184 9.32617 25.7422C9.32617 25.166 9.79492 24.707 10.3711 24.707C10.9473 24.707 11.416 25.166 11.416 25.7422C11.416 26.3184 10.9473 26.7969 10.3711 26.7969ZM10.3613 2.8418C9.99023 2.8418 9.69727 2.53906 9.69727 2.17773C9.69727 1.82617 9.99023 1.51367 10.3613 1.51367C10.7129 1.51367 11.0254 1.82617 11.0254 2.17773C11.0254 2.53906 10.7129 2.8418 10.3613 2.8418Z", fillAlpha = 0.85f)
        }
        return _sFIpadGen1!!
    }

private var _sFIpadGen1: ImageVector? = null
