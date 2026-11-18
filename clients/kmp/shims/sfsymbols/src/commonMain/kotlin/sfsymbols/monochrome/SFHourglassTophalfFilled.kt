package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHourglassTophalfFilled (monochrome)
 * Viewport: 14.9707 x 22.9199
 */
public val SfSymbols.Monochrome.SFHourglassTophalfFilled: ImageVector
    get() {
        if (_sFHourglassTophalfFilled != null) {
            return _sFHourglassTophalfFilled!!
        }
        _sFHourglassTophalfFilled = sfIcon(
            name = "Monochrome.SFHourglassTophalfFilled",
            viewportWidth = 14.9707f,
            viewportHeight = 22.9199f
        ) {
            addSfPath("M14.6094 1.94336L14.6094 2.20703C14.6094 5.17578 11.3867 8.45703 9.51172 10.3223C8.75977 11.0742 8.80859 11.8457 9.51172 12.5488C11.3867 14.4141 14.6094 17.7148 14.6094 20.6641L14.6094 20.957C14.6094 22.207 13.8672 22.9102 12.6758 22.9102L1.93359 22.9102C0.742188 22.9102 0 22.207 0 20.957L0 20.6641C0 17.7148 3.21289 14.4141 5.08789 12.5488C5.83984 11.7969 5.79102 11.0254 5.08789 10.3223C3.21289 8.45703 0 5.17578 0 2.20703L0 1.94336C0 0.693359 0.742188 0 1.93359 0L12.6758 0C13.8672 0 14.6094 0.693359 14.6094 1.94336ZM6.74805 16.3965L2.55859 20.459C2.2168 20.7812 2.35352 21.3477 2.8418 21.3477L11.7676 21.3477C12.2559 21.3477 12.3828 20.7812 12.041 20.459L7.85156 16.3965C7.68066 16.2305 7.49512 16.145 7.30713 16.1438C7.11914 16.1426 6.92871 16.2256 6.74805 16.3965Z", fillAlpha = 0.85f)
        }
        return _sFHourglassTophalfFilled!!
    }

private var _sFHourglassTophalfFilled: ImageVector? = null
