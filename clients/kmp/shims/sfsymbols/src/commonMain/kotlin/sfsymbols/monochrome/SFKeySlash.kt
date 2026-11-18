package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeySlash (monochrome)
 * Viewport: 28.9893 x 31.1816
 */
public val SfSymbols.Monochrome.SFKeySlash: ImageVector
    get() {
        if (_sFKeySlash != null) {
            return _sFKeySlash!!
        }
        _sFKeySlash = sfIcon(
            name = "Monochrome.SFKeySlash",
            viewportWidth = 28.9893f,
            viewportHeight = 31.1816f
        ) {
            addSfPath("M12.8149 18.4908L12.8149 27.5195L14.3188 29.0137L17.2095 26.084L15.2759 24.1602L15.2759 22.3828L15.9958 21.6703L17.2375 22.9114L16.8579 23.291L18.938 25.3613C19.3481 25.7715 19.3677 26.416 18.9478 26.8457L15.061 30.7324C14.6313 31.1523 14.0259 31.1035 13.5767 30.7227L11.5259 28.8672C11.1938 28.5742 11.0864 28.1543 11.0864 27.7832L11.0864 16.7631ZM22.5513 8.21289C22.5513 11.5465 20.5684 14.3481 17.0346 15.9621L15.6862 14.6142C18.8698 13.6966 20.813 11.1527 20.813 8.21289C20.813 4.61914 17.9224 1.73828 14.3188 1.73828C11.168 1.73828 8.55932 3.93629 7.94451 6.87593L6.54759 5.47961C7.66145 2.27477 10.7043 0 14.3188 0C18.8696 0 22.5513 3.66211 22.5513 8.21289ZM16.3208 5.75195C16.3208 6.85547 15.4224 7.75391 14.3188 7.75391C13.2056 7.75391 12.3169 6.85547 12.3169 5.75195C12.3169 4.63867 13.1958 3.75 14.3188 3.75C15.4224 3.75 16.3208 4.63867 16.3208 5.75195Z", fillAlpha = 0.85f)
            addSfPath("M1.76025 5.25391L24.2896 27.7637C24.6216 28.0957 25.1489 28.0957 25.4712 27.7637C25.7935 27.4316 25.7935 26.9141 25.4712 26.5918L2.95166 4.08203C2.62939 3.75977 2.09229 3.75 1.76025 4.08203C1.44775 4.39453 1.44775 4.94141 1.76025 5.25391Z", fillAlpha = 0.85f)
        }
        return _sFKeySlash!!
    }

private var _sFKeySlash: ImageVector? = null
