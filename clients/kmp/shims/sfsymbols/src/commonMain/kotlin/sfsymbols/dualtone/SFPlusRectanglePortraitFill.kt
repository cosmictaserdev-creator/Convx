package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusRectanglePortraitFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFPlusRectanglePortraitFill: ImageVector
    get() {
        if (_sFPlusRectanglePortraitFill != null) {
            return _sFPlusRectanglePortraitFill!!
        }
        _sFPlusRectanglePortraitFill = sfIcon(
            name = "Dualtone.SFPlusRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859Z", fillAlpha = 0.2125f)
            addSfPath("M11.4258 18.4277L11.4258 8.48633C11.4258 7.92969 11.0352 7.53906 10.4785 7.53906C9.94141 7.53906 9.56055 7.92969 9.56055 8.48633L9.56055 18.4277C9.56055 18.9648 9.94141 19.3652 10.4785 19.3652C11.0352 19.3652 11.4258 18.9746 11.4258 18.4277ZM5.52734 14.3848L15.4688 14.3848C16.0059 14.3848 16.4062 14.0039 16.4062 13.4668C16.4062 12.9102 16.0156 12.5195 15.4688 12.5195L5.52734 12.5195C4.9707 12.5195 4.57031 12.9102 4.57031 13.4668C4.57031 14.0039 4.9707 14.3848 5.52734 14.3848Z", fillAlpha = 0.85f)
        }
        return _sFPlusRectanglePortraitFill!!
    }

private var _sFPlusRectanglePortraitFill: ImageVector? = null
