package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhoneDownCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPhoneDownCircleFill: ImageVector
    get() {
        if (_sFPhoneDownCircleFill != null) {
            return _sFPhoneDownCircleFill!!
        }
        _sFPhoneDownCircleFill = sfIcon(
            name = "Monochrome.SFPhoneDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM5.44922 11.5527C4.80469 12.1875 4.46289 12.9785 4.48242 13.7988C4.49219 14.3848 4.63867 15.0586 4.9707 15.3906C5.25391 15.6543 5.63477 15.6445 6.2207 15.5469L8.25195 15.2148C8.73047 15.127 9.00391 14.9902 9.20898 14.7852C9.375 14.6094 9.44336 14.4531 9.54102 14.1016L9.89258 12.8223C9.91211 12.6953 9.94141 12.6074 10 12.5586C10.0977 12.4707 10.1953 12.4219 10.2734 12.3828C10.8594 12.1973 11.7383 12.0801 12.6855 12.0801C13.6523 12.0801 14.5898 12.1973 15.1953 12.4023C15.3125 12.4707 15.4004 12.5098 15.5078 12.5977C15.5664 12.666 15.5957 12.7246 15.6445 12.8711L16.0156 14.1211C16.1328 14.541 16.2305 14.6973 16.3867 14.8535C16.5723 15.0391 16.748 15.127 17.2852 15.2148L19.4043 15.5762C19.9707 15.6738 20.3027 15.6055 20.5176 15.4004C20.8008 15.1172 20.9863 14.6875 21.0254 13.8965C21.0645 13.0762 20.752 12.2168 20.0781 11.5527C18.6328 10.1172 15.7129 9.46289 12.7344 9.46289C9.74609 9.46289 6.88477 10.1074 5.44922 11.5527Z", fillAlpha = 0.85f)
        }
        return _sFPhoneDownCircleFill!!
    }

private var _sFPhoneDownCircleFill: ImageVector? = null
