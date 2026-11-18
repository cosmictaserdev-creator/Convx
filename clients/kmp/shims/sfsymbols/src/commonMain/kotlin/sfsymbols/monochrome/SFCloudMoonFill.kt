package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudMoonFill (monochrome)
 * Viewport: 34.4336 x 23.2715
 */
public val SfSymbols.Monochrome.SFCloudMoonFill: ImageVector
    get() {
        if (_sFCloudMoonFill != null) {
            return _sFCloudMoonFill!!
        }
        _sFCloudMoonFill = sfIcon(
            name = "Monochrome.SFCloudMoonFill",
            viewportWidth = 34.4336f,
            viewportHeight = 23.2715f
        ) {
            addSfPath("M26.123 1.45508C26.123 2.05078 25.2832 3.25195 25.2832 4.89258C25.2832 7.90039 27.1387 9.66797 30.3418 9.66797C32.0312 9.66797 33.0371 8.92578 33.584 8.92578C33.9551 8.92578 34.1602 9.23828 34.1602 9.66797C34.1602 11.4547 32.1171 14.9343 27.895 15.4457C27.746 11.6354 24.7729 8.75899 20.8105 8.37891C20.4719 7.8312 20.0944 7.32766 19.6819 6.8717C20.2386 3.5394 22.9531 1.02577 25.3809 0.869141C25.8691 0.839844 26.123 1.07422 26.123 1.45508Z", fillAlpha = 0.85f)
            addSfPath("M6.54297 21.8359L20.2246 21.8359C23.7402 21.8359 26.4648 19.1797 26.4648 15.7617C26.4648 12.3535 23.6816 9.78516 19.9609 9.77539C18.4668 6.9043 15.8789 5.27344 12.832 5.27344C8.78906 5.27344 5.36133 8.45703 5 12.5879C2.82227 13.2129 1.43555 15 1.43555 17.1875C1.43555 19.9414 3.51562 21.8359 6.54297 21.8359Z", fillAlpha = 0.85f)
        }
        return _sFCloudMoonFill!!
    }

private var _sFCloudMoonFill: ImageVector? = null
