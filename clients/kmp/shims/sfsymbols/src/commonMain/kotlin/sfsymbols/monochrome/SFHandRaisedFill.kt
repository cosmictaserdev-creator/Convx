package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHandRaisedFill (monochrome)
 * Viewport: 21.2402 x 27.4902
 */
public val SfSymbols.Monochrome.SFHandRaisedFill: ImageVector
    get() {
        if (_sFHandRaisedFill != null) {
            return _sFHandRaisedFill!!
        }
        _sFHandRaisedFill = sfIcon(
            name = "Monochrome.SFHandRaisedFill",
            viewportWidth = 21.2402f,
            viewportHeight = 27.4902f
        ) {
            addSfPath("M1.58203 17.8906C1.58203 23.4766 5.21484 26.6406 10.0879 26.6406C14.0723 26.6406 16.6797 24.873 18.2812 21.1426C19.1602 19.2578 19.8633 17.0703 20.5859 14.5996C20.8691 13.7012 21.2402 12.5977 21.2402 12.0898C21.2402 11.4258 20.752 11.0059 20.0781 11.0059C19.3652 11.0059 18.9062 11.4355 18.3984 12.5L16.6992 16.2402C16.5039 16.6895 16.2695 16.8457 16.0352 16.8457C15.7422 16.8457 15.5273 16.6699 15.5273 16.1621L15.5273 3.23242C15.5273 2.5293 14.9512 1.94336 14.2383 1.94336C13.5352 1.94336 12.9492 2.5293 12.9492 3.23242L12.9492 12.4316C12.5977 12.2949 12.207 12.1777 11.7871 12.0996L11.7871 1.28906C11.7871 0.585938 11.2012 0 10.498 0C9.78516 0 9.20898 0.585938 9.20898 1.28906L9.20898 11.9531C8.76953 11.9922 8.33984 12.041 7.91992 12.1191L7.91992 2.44141C7.91992 1.73828 7.34375 1.16211 6.63086 1.16211C5.92773 1.16211 5.3418 1.73828 5.3418 2.44141L5.3418 12.9492C4.90234 13.1543 4.51172 13.3887 4.16016 13.6328L4.16016 6.33789C4.16016 5.63477 3.57422 5.04883 2.87109 5.04883C2.16797 5.04883 1.58203 5.63477 1.58203 6.33789Z", fillAlpha = 0.85f)
        }
        return _sFHandRaisedFill!!
    }

private var _sFHandRaisedFill: ImageVector? = null
