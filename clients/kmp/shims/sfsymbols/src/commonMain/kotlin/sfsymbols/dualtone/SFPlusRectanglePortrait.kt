package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusRectanglePortrait (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFPlusRectanglePortrait: ImageVector
    get() {
        if (_sFPlusRectanglePortrait != null) {
            return _sFPlusRectanglePortrait!!
        }
        _sFPlusRectanglePortrait = sfIcon(
            name = "Dualtone.SFPlusRectanglePortrait",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859Z", fillAlpha = 0.425f)
            addSfPath("M11.3867 18.291L11.3867 8.59375C11.3867 8.04688 11.0156 7.66602 10.4785 7.66602C9.96094 7.66602 9.58984 8.04688 9.58984 8.59375L9.58984 18.291C9.58984 18.8281 9.96094 19.209 10.4785 19.209C11.0156 19.209 11.3867 18.8379 11.3867 18.291ZM5.64453 14.3359L15.3516 14.3359C15.8789 14.3359 16.2598 13.9746 16.2598 13.457C16.2598 12.9102 15.8887 12.5391 15.3516 12.5391L5.64453 12.5391C5.09766 12.5391 4.72656 12.9102 4.72656 13.457C4.72656 13.9746 5.09766 14.3359 5.64453 14.3359Z", fillAlpha = 0.85f)
        }
        return _sFPlusRectanglePortrait!!
    }

private var _sFPlusRectanglePortrait: ImageVector? = null
