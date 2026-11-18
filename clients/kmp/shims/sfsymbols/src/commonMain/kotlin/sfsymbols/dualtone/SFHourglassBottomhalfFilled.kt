package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHourglassBottomhalfFilled (dualtone)
 * Viewport: 14.9707 x 22.9199
 */
public val SfSymbols.Dualtone.SFHourglassBottomhalfFilled: ImageVector
    get() {
        if (_sFHourglassBottomhalfFilled != null) {
            return _sFHourglassBottomhalfFilled!!
        }
        _sFHourglassBottomhalfFilled = sfIcon(
            name = "Dualtone.SFHourglassBottomhalfFilled",
            viewportWidth = 14.9707f,
            viewportHeight = 22.9199f
        ) {
            addSfPath("M1.93359 22.9102L12.6758 22.9102C13.8672 22.9102 14.6094 22.207 14.6094 20.957L14.6094 20.6641C14.6094 17.7148 11.3867 14.4141 9.51172 12.5488C8.80859 11.8457 8.75977 11.0742 9.51172 10.3223C11.3867 8.45703 14.6094 5.17578 14.6094 2.20703L14.6094 1.94336C14.6094 0.693359 13.8672 0 12.6758 0L1.93359 0C0.742188 0 0 0.693359 0 1.94336L0 2.20703C0 5.17578 3.21289 8.45703 5.08789 10.3223C5.83984 11.0742 5.79102 11.8457 5.08789 12.5488C3.21289 14.4141 0 17.7148 0 20.6641L0 20.957C0 22.207 0.742188 22.9102 1.93359 22.9102ZM8.07617 9.69727C7.50977 10.166 7.08984 10.166 6.5332 9.69727C5.49805 8.81836 2.91992 6.12305 2.02148 4.48242C1.81641 4.13086 1.93359 3.83789 2.28516 3.83789L12.3145 3.83789C12.6758 3.83789 12.7832 4.13086 12.5879 4.48242C11.6895 6.12305 9.10156 8.81836 8.07617 9.69727Z", fillAlpha = 0.85f)
            addSfPath("M8.07617 9.69727C7.50977 10.166 7.08984 10.166 6.5332 9.69727C5.49805 8.81836 2.91992 6.12305 2.02148 4.48242C1.81641 4.13086 1.93359 3.83789 2.28516 3.83789L12.3145 3.83789C12.6758 3.83789 12.7832 4.13086 12.5879 4.48242C11.6895 6.12305 9.10156 8.81836 8.07617 9.69727Z", fillAlpha = 0.2125f)
        }
        return _sFHourglassBottomhalfFilled!!
    }

private var _sFHourglassBottomhalfFilled: ImageVector? = null
