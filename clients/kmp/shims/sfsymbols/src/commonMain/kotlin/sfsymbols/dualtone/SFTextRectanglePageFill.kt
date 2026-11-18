package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextRectanglePageFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFTextRectanglePageFill: ImageVector
    get() {
        if (_sFTextRectanglePageFill != null) {
            return _sFTextRectanglePageFill!!
        }
        _sFTextRectanglePageFill = sfIcon(
            name = "Dualtone.SFTextRectanglePageFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M20.9668 3.80859L20.9668 23.125C20.9668 25.6445 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.28906 20.9668 3.80859ZM5.56641 12.0508C4.46289 12.0508 3.84766 12.6172 3.84766 13.7598L3.84766 21.3281C3.84766 22.4707 4.46289 23.0469 5.56641 23.0469L15.4102 23.0469C16.543 23.0469 17.1191 22.4707 17.1191 21.3281L17.1191 13.7598C17.1191 12.6172 16.543 12.0508 15.4102 12.0508ZM5.35156 8.36914C4.94141 8.36914 4.63867 8.67188 4.63867 9.05273C4.63867 9.44336 4.94141 9.75586 5.35156 9.75586L11.7578 9.75586C12.168 9.75586 12.4805 9.44336 12.4805 9.05273C12.4805 8.67188 12.168 8.36914 11.7578 8.36914ZM5.35156 4.66797C4.94141 4.66797 4.63867 4.9707 4.63867 5.37109C4.63867 5.76172 4.94141 6.07422 5.35156 6.07422L15.9961 6.07422C16.3965 6.07422 16.6992 5.76172 16.6992 5.37109C16.6992 4.9707 16.3965 4.66797 15.9961 4.66797Z", fillAlpha = 0.85f)
        }
        return _sFTextRectanglePageFill!!
    }

private var _sFTextRectanglePageFill: ImageVector? = null
