package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusminus (dualtone)
 * Viewport: 15.625 x 20.8887
 */
public val SfSymbols.Dualtone.SFPlusminus: ImageVector
    get() {
        if (_sFPlusminus != null) {
            return _sFPlusminus!!
        }
        _sFPlusminus = sfIcon(
            name = "Dualtone.SFPlusminus",
            viewportWidth = 15.625f,
            viewportHeight = 20.8887f
        ) {
            addSfPath("M0 19.9316C0 20.4492 0.439453 20.8887 0.957031 20.8887L14.3066 20.8887C14.8242 20.8887 15.2637 20.4492 15.2637 19.9316C15.2637 19.4043 14.8242 18.9746 14.3066 18.9746L0.957031 18.9746C0.439453 18.9746 0 19.4043 0 19.9316Z", fillAlpha = 0.85f)
            addSfPath("M7.62695 14.7852C8.14453 14.7852 8.57422 14.3652 8.57422 13.8477L8.57422 0.9375C8.57422 0.419922 8.14453 0 7.62695 0C7.09961 0 6.66992 0.419922 6.66992 0.9375L6.66992 13.8477C6.66992 14.3652 7.09961 14.7852 7.62695 14.7852ZM0 7.37305C0 7.89062 0.429688 8.32031 0.957031 8.32031L14.3066 8.32031C14.834 8.32031 15.2637 7.89062 15.2637 7.37305C15.2637 6.8457 14.834 6.41602 14.3066 6.41602L0.957031 6.41602C0.429688 6.41602 0 6.8457 0 7.37305Z", fillAlpha = 0.85f)
        }
        return _sFPlusminus!!
    }

private var _sFPlusminus: ImageVector? = null
