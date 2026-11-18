package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrownFill (monochrome)
 * Viewport: 31.9824 x 25.3613
 */
public val SfSymbols.Monochrome.SFCrownFill: ImageVector
    get() {
        if (_sFCrownFill != null) {
            return _sFCrownFill!!
        }
        _sFCrownFill = sfIcon(
            name = "Monochrome.SFCrownFill",
            viewportWidth = 31.9824f,
            viewportHeight = 25.3613f
        ) {
            addSfPath("M5.37109 19.6875L5.74219 21.1719C6.21094 23.0957 7.2168 24.043 9.20898 24.043L22.4121 24.043C24.4141 24.043 25.4199 23.1152 25.8887 21.1719L26.2598 19.6875ZM4.99023 18.125L26.6504 18.125L28.9453 8.74023L27.9395 8.125L22.5391 11.9629C22.0508 12.2949 21.6895 12.1875 21.3965 11.7773L16.1914 4.13086L15.4492 4.11133L10.166 11.8457C9.90234 12.2168 9.58984 12.3047 9.14062 11.9922L3.7793 8.19336L2.64648 8.58398ZM2.91992 10.791C4.52148 10.791 5.83008 9.48242 5.83008 7.87109C5.83008 6.2793 4.52148 4.96094 2.91992 4.96094C1.30859 4.96094 0 6.26953 0 7.87109C0 9.48242 1.31836 10.791 2.91992 10.791ZM15.8105 5.82031C17.4219 5.82031 18.7207 4.52148 18.7207 2.91016C18.7207 1.30859 17.4219 0 15.8105 0C14.1992 0 12.8906 1.29883 12.8906 2.91016C12.8906 4.52148 14.209 5.82031 15.8105 5.82031ZM28.7012 10.791C30.3027 10.791 31.6211 9.48242 31.6211 7.87109C31.6211 6.26953 30.3125 4.96094 28.7012 4.96094C27.0898 4.96094 25.791 6.2793 25.791 7.87109C25.791 9.48242 27.0898 10.791 28.7012 10.791Z", fillAlpha = 0.85f)
        }
        return _sFCrownFill!!
    }

private var _sFCrownFill: ImageVector? = null
