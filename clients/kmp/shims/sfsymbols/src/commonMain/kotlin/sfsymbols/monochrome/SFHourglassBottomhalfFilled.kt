package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHourglassBottomhalfFilled (monochrome)
 * Viewport: 14.9707 x 22.9199
 */
public val SfSymbols.Monochrome.SFHourglassBottomhalfFilled: ImageVector
    get() {
        if (_sFHourglassBottomhalfFilled != null) {
            return _sFHourglassBottomhalfFilled!!
        }
        _sFHourglassBottomhalfFilled = sfIcon(
            name = "Monochrome.SFHourglassBottomhalfFilled",
            viewportWidth = 14.9707f,
            viewportHeight = 22.9199f
        ) {
            addSfPath("M14.6094 1.94336L14.6094 2.20703C14.6094 5.17578 11.3867 8.45703 9.51172 10.3223C8.75977 11.0742 8.80859 11.8457 9.51172 12.5488C11.3867 14.4141 14.6094 17.7148 14.6094 20.6641L14.6094 20.957C14.6094 22.207 13.8672 22.9102 12.6758 22.9102L1.93359 22.9102C0.742188 22.9102 0 22.207 0 20.957L0 20.6641C0 17.7148 3.21289 14.4141 5.08789 12.5488C5.79102 11.8457 5.83984 11.0742 5.08789 10.3223C3.21289 8.45703 0 5.17578 0 2.20703L0 1.94336C0 0.693359 0.742188 0 1.93359 0L12.6758 0C13.8672 0 14.6094 0.693359 14.6094 1.94336ZM2.02148 4.48242C2.91992 6.12305 5.49805 8.81836 6.5332 9.69727C6.81152 9.93164 7.05566 10.0488 7.30103 10.0488C7.54639 10.0488 7.79297 9.93164 8.07617 9.69727C9.10156 8.81836 11.6895 6.12305 12.5879 4.48242C12.7832 4.13086 12.6758 3.83789 12.3145 3.83789L2.28516 3.83789C1.93359 3.83789 1.81641 4.13086 2.02148 4.48242Z", fillAlpha = 0.85f)
        }
        return _sFHourglassBottomhalfFilled!!
    }

private var _sFHourglassBottomhalfFilled: ImageVector? = null
