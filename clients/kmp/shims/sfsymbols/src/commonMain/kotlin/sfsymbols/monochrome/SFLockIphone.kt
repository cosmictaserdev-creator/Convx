package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockIphone (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFLockIphone: ImageVector
    get() {
        if (_sFLockIphone != null) {
            return _sFLockIphone!!
        }
        _sFLockIphone = sfIcon(
            name = "Monochrome.SFLockIphone",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.85f)
            addSfPath("M5.68359 17.373L9.98047 17.373C10.6738 17.373 11.0254 17.002 11.0254 16.2598L11.0254 12.8711C11.0254 12.2656 10.7812 11.9043 10.3027 11.7871L10.3027 11.0254C10.3027 9.4043 9.30664 8.28125 7.83203 8.28125C6.35742 8.28125 5.36133 9.4043 5.36133 11.0254L5.36133 11.7969C4.88281 11.9043 4.63867 12.2656 4.63867 12.8711L4.63867 16.2598C4.63867 17.002 4.98047 17.373 5.68359 17.373ZM6.49414 11.7578L6.49414 10.9375C6.49414 10 7.01172 9.38477 7.83203 9.38477C8.64258 9.38477 9.16992 10 9.16992 10.9375L9.16992 11.7578Z", fillAlpha = 0.85f)
        }
        return _sFLockIphone!!
    }

private var _sFLockIphone: ImageVector? = null
