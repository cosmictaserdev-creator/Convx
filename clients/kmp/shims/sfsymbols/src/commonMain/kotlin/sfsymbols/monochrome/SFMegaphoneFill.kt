package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMegaphoneFill (monochrome)
 * Viewport: 27.9199 x 26.6309
 */
public val SfSymbols.Monochrome.SFMegaphoneFill: ImageVector
    get() {
        if (_sFMegaphoneFill != null) {
            return _sFMegaphoneFill!!
        }
        _sFMegaphoneFill = sfIcon(
            name = "Monochrome.SFMegaphoneFill",
            viewportWidth = 27.9199f,
            viewportHeight = 26.6309f
        ) {
            addSfPath("M5.0293 18.1348L8.01758 18.1348L8.01758 6.41602L5.0293 6.41602C1.74805 6.41602 0 8.125 0 11.2988L0 13.252C0 16.4258 1.74805 18.1348 5.0293 18.1348ZM9.58008 18.1445C13.2129 18.5352 17.7344 19.5898 21.1035 21.3574C21.1035 21.2305 21.1035 21.1035 21.1035 20.9863L21.1035 3.60352C21.1035 3.4668 21.1035 3.33984 21.1035 3.19336C17.7051 4.98047 12.959 6.05469 9.58008 6.41602ZM25.1074 24.4824C26.5723 24.4824 27.5586 23.4961 27.5586 22.041L27.5586 2.45117C27.5586 0.996094 26.5723 0 25.1074 0C23.6621 0 22.666 0.996094 22.666 2.45117L22.666 22.041C22.666 23.4961 23.6621 24.4824 25.1074 24.4824ZM10.1465 26.6113C11.6797 26.6113 12.8027 25.5371 12.4121 23.6133L11.6895 20.0488C10.8008 19.8926 9.55078 19.7949 6.73828 19.7266C6.41602 19.7168 5.80078 19.6875 5.16602 19.6875L7.37305 24.4824C7.99805 25.8594 8.75977 26.6113 10.1465 26.6113Z", fillAlpha = 0.85f)
        }
        return _sFMegaphoneFill!!
    }

private var _sFMegaphoneFill: ImageVector? = null
