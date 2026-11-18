package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowermeter (monochrome)
 * Viewport: 27.8031 x 24.0332
 */
public val SfSymbols.Monochrome.SFPowermeter: ImageVector
    get() {
        if (_sFPowermeter != null) {
            return _sFPowermeter!!
        }
        _sFPowermeter = sfIcon(
            name = "Monochrome.SFPowermeter",
            viewportWidth = 27.8031f,
            viewportHeight = 24.0332f
        ) {
            addSfPath("M13.7209 0C6.84588 0 1.14275 4.98047 0.019705 11.5332C-0.0877169 12.1191 0.25408 12.5879 0.800955 12.6367C1.30877 12.6855 1.76775 12.3047 1.86541 11.7383C2.85174 6.06445 7.73455 1.81641 13.7209 1.81641C20.4299 1.81641 25.7717 7.16797 25.7717 13.8965C25.7717 17.1582 24.4924 20.1758 22.4025 22.334C21.9631 22.793 21.9045 23.3691 22.2267 23.7012C22.6369 24.082 23.2033 24.0625 23.5842 23.6816C26.0842 21.1719 27.6174 17.6953 27.6174 13.8965C27.6174 6.21094 21.3967 0 13.7209 0Z", fillAlpha = 0.85f)
            addSfPath("M5.20525 23.7012C5.53728 23.3691 5.46892 22.793 5.03924 22.334C3.40838 20.6543 2.27556 18.4668 1.86541 16.0449C1.76775 15.4883 1.30877 15.0977 0.800955 15.1367C0.25408 15.1953-0.0877169 15.6641 0.019705 16.25C0.498221 19.1113 1.87517 21.6895 3.8576 23.6816C4.22869 24.0625 4.80486 24.082 5.20525 23.7012Z", fillAlpha = 0.85f)
            addSfPath("M12.3342 15.5664C13.174 16.416 14.4142 16.416 15.2541 15.5664C16.1037 14.7363 16.1037 13.4961 15.2541 12.6465C15.1076 12.4902 14.7951 12.2559 14.59 12.1191L7.82244 7.33398C7.53924 7.12891 7.24627 7.1875 7.05095 7.36328C6.86541 7.55859 6.81658 7.8418 7.03142 8.14453L11.8068 14.9023C11.9533 15.1074 12.1779 15.4297 12.3342 15.5664Z", fillAlpha = 0.85f)
        }
        return _sFPowermeter!!
    }

private var _sFPowermeter: ImageVector? = null
