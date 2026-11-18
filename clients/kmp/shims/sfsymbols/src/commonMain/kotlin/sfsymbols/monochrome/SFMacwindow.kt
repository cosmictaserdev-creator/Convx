package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMacwindow (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFMacwindow: ImageVector
    get() {
        if (_sFMacwindow != null) {
            return _sFMacwindow!!
        }
        _sFMacwindow = sfIcon(
            name = "Monochrome.SFMacwindow",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M5.08789 22.959L24.7461 22.959C27.8027 22.959 29.5898 21.0938 29.5898 17.8809L29.5898 5.08789C29.5898 1.86523 27.8027 0 24.7461 0L5.08789 0C1.86523 0 0 1.86523 0 5.08789L0 17.8809C0 21.0938 1.86523 22.959 5.08789 22.959ZM5.09766 21.2305C2.97852 21.2305 1.72852 19.9805 1.72852 17.8613L1.72852 5.09766C1.72852 2.97852 2.97852 1.72852 5.09766 1.72852L24.4922 1.72852C26.6113 1.72852 27.8516 2.97852 27.8516 5.09766L27.8516 17.8613C27.8516 19.9805 26.6113 21.2305 24.4922 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M5.29297 6.2207C5.86914 6.2207 6.33789 5.72266 6.33789 5.17578C6.33789 4.61914 5.86914 4.12109 5.29297 4.12109C4.7168 4.12109 4.24805 4.61914 4.24805 5.17578C4.24805 5.72266 4.7168 6.2207 5.29297 6.2207Z", fillAlpha = 0.85f)
            addSfPath("M8.7207 6.2207C9.29688 6.2207 9.76562 5.73242 9.76562 5.17578C9.76562 4.61914 9.29688 4.12109 8.7207 4.12109C8.14453 4.12109 7.66602 4.61914 7.66602 5.17578C7.66602 5.73242 8.14453 6.2207 8.7207 6.2207Z", fillAlpha = 0.85f)
            addSfPath("M12.1484 6.21094C12.7246 6.21094 13.1934 5.73242 13.1934 5.17578C13.1934 4.61914 12.7246 4.12109 12.1484 4.12109C11.5723 4.12109 11.0938 4.61914 11.0938 5.17578C11.0938 5.73242 11.5723 6.21094 12.1484 6.21094Z", fillAlpha = 0.85f)
        }
        return _sFMacwindow!!
    }

private var _sFMacwindow: ImageVector? = null
