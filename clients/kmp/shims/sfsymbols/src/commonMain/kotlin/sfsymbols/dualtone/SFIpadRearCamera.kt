package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpadRearCamera (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFIpadRearCamera: ImageVector
    get() {
        if (_sFIpadRearCamera != null) {
            return _sFIpadRearCamera!!
        }
        _sFIpadRearCamera = sfIcon(
            name = "Dualtone.SFIpadRearCamera",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.425f)
            addSfPath("M4.89258 6.41602C5.70312 6.41602 6.40625 5.71289 6.40625 4.86328C6.40625 4.04297 5.70312 3.33984 4.89258 3.33984C4.0332 3.33984 3.33984 4.04297 3.33984 4.86328C3.33984 5.71289 4.0332 6.41602 4.89258 6.41602Z", fillAlpha = 0.85f)
        }
        return _sFIpadRearCamera!!
    }

private var _sFIpadRearCamera: ImageVector? = null
