package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeLFill (dualtone)
 * Viewport: 13.877 x 27.0996
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeLFill: ImageVector
    get() {
        if (_sFPoweroutletTypeLFill != null) {
            return _sFPoweroutletTypeLFill!!
        }
        _sFPoweroutletTypeLFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeLFill",
            viewportWidth = 13.877f,
            viewportHeight = 27.0996f
        ) {
            addSfPath("M3.68164 27.0996L9.83398 27.0996C12.2168 27.0996 13.5156 25.8105 13.5156 23.457L13.5156 3.65234C13.5156 1.28906 12.2168 0.00976562 9.83398 0.00976562L3.68164 0.00976562C1.29883 0.00976562 0 1.28906 0 3.65234L0 23.457C0 25.8105 1.29883 27.0996 3.68164 27.0996Z", fillAlpha = 0.2125f)
            addSfPath("M6.76758 8.36914C5.72266 8.36914 4.88281 7.5293 4.88281 6.48438C4.88281 5.45898 5.72266 4.61914 6.76758 4.61914C7.80273 4.61914 8.64258 5.45898 8.64258 6.48438C8.64258 7.5293 7.80273 8.36914 6.76758 8.36914ZM6.76758 15.4297C5.72266 15.4297 4.88281 14.5801 4.88281 13.5449C4.88281 12.5098 5.72266 11.6699 6.76758 11.6699C7.80273 11.6699 8.64258 12.5098 8.64258 13.5449C8.64258 14.5801 7.80273 15.4297 6.76758 15.4297ZM6.76758 22.4805C5.72266 22.4805 4.88281 21.6309 4.88281 20.6055C4.88281 19.5703 5.72266 18.7305 6.76758 18.7305C7.80273 18.7305 8.64258 19.5703 8.64258 20.6055C8.64258 21.6309 7.80273 22.4805 6.76758 22.4805Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeLFill!!
    }

private var _sFPoweroutletTypeLFill: ImageVector? = null
