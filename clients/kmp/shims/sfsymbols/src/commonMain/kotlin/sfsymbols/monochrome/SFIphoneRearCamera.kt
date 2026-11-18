package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneRearCamera (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFIphoneRearCamera: ImageVector
    get() {
        if (_sFIphoneRearCamera != null) {
            return _sFIphoneRearCamera!!
        }
        _sFIphoneRearCamera = sfIcon(
            name = "Monochrome.SFIphoneRearCamera",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.85f)
            addSfPath("M4.89258 6.41602C5.70312 6.41602 6.40625 5.71289 6.40625 4.86328C6.40625 4.04297 5.70312 3.33984 4.89258 3.33984C4.0332 3.33984 3.33984 4.04297 3.33984 4.86328C3.33984 5.71289 4.0332 6.41602 4.89258 6.41602Z", fillAlpha = 0.85f)
        }
        return _sFIphoneRearCamera!!
    }

private var _sFIphoneRearCamera: ImageVector? = null
