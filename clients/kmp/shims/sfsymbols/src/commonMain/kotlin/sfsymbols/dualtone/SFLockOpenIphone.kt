package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockOpenIphone (dualtone)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Dualtone.SFLockOpenIphone: ImageVector
    get() {
        if (_sFLockOpenIphone != null) {
            return _sFLockOpenIphone!!
        }
        _sFLockOpenIphone = sfIcon(
            name = "Dualtone.SFLockOpenIphone",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.425f)
            addSfPath("M3.35938 16.2988C3.35938 17.002 3.69141 17.3535 4.36523 17.3535L8.63281 17.3535C9.30664 17.3535 9.63867 17.002 9.63867 16.2988L9.63867 13.0176C9.63867 12.3047 9.30664 11.9531 8.63281 11.9531L4.36523 11.9531C3.69141 11.9531 3.35938 12.3047 3.35938 13.0176ZM7.71484 12.5684L8.84766 12.5684L8.84766 10.8984C8.84766 9.92188 9.375 9.29688 10.2637 9.29688C11.1523 9.29688 11.6895 9.92188 11.6895 10.8984L11.6895 11.7188C11.6895 12.0312 11.9434 12.2949 12.2559 12.2949C12.5586 12.2949 12.8125 12.0312 12.8125 11.7188L12.8125 10.8984C12.8125 9.32617 11.8066 8.19336 10.2637 8.19336C8.73047 8.19336 7.71484 9.32617 7.71484 10.9863Z", fillAlpha = 0.85f)
        }
        return _sFLockOpenIphone!!
    }

private var _sFLockOpenIphone: ImageVector? = null
