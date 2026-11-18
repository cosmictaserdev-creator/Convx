package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSnowboardFill (monochrome)
 * Viewport: 30.11 x 29.8047
 */
public val SfSymbols.Monochrome.SFSnowboardFill: ImageVector
    get() {
        if (_sFSnowboardFill != null) {
            return _sFSnowboardFill!!
        }
        _sFSnowboardFill = sfIcon(
            name = "Monochrome.SFSnowboardFill",
            viewportWidth = 30.11f,
            viewportHeight = 29.8047f
        ) {
            addSfPath("M1.92513 27.8174C4.58138 30.4541 7.88216 30.3857 10.6458 27.6221C11.9056 26.3623 14.3958 22.6611 18.5267 18.5205C22.6576 14.3896 26.3685 11.9092 27.6283 10.6494C30.3822 7.87596 30.4603 4.57518 27.8236 1.9287C25.1673-0.708022 21.8763-0.639662 19.1029 2.12401C17.8431 3.38378 15.3626 7.09471 11.222 11.2256C7.09114 15.3564 3.38997 17.8369 2.12044 19.0967C-0.633465 21.8701-0.71159 25.1709 1.92513 27.8174ZM15.3431 12.7099C14.8548 12.2314 14.8353 11.4697 15.3236 10.9912C15.7923 10.5127 16.5638 10.5127 17.0521 10.9912L18.7513 12.7002C19.2298 13.1885 19.2396 13.9404 18.7611 14.4092C18.2728 14.8974 17.5208 14.9072 17.0423 14.4092ZM10.9779 17.0752C10.4993 16.5967 10.4798 15.8252 10.9681 15.3467C11.4466 14.8682 12.1986 14.8779 12.6771 15.3662L14.3861 17.0752C14.8743 17.5537 14.8841 18.2959 14.3958 18.7842C13.9271 19.2627 13.1556 19.2627 12.6771 18.7744Z", fillAlpha = 0.85f)
        }
        return _sFSnowboardFill!!
    }

private var _sFSnowboardFill: ImageVector? = null
