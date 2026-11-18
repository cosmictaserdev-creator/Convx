package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudFill (dualtone)
 * Viewport: 30.2734 x 20.127
 */
public val SfSymbols.Dualtone.SFCloudFill: ImageVector
    get() {
        if (_sFCloudFill != null) {
            return _sFCloudFill!!
        }
        _sFCloudFill = sfIcon(
            name = "Dualtone.SFCloudFill",
            viewportWidth = 30.2734f,
            viewportHeight = 20.127f
        ) {
            addSfPath("M6.34766 19.7852L22.832 19.7852C27.0215 19.7852 30.2734 16.6113 30.2734 12.5684C30.2734 8.4668 26.9043 5.41016 22.4316 5.42969C20.7715 2.07031 17.7441 0 13.9258 0C9.04297 0 4.99023 3.86719 4.57031 8.80859C2.07031 9.50195 0.283203 11.5918 0.283203 14.2773C0.283203 17.4707 2.65625 19.7852 6.34766 19.7852Z", fillAlpha = 0.85f)
        }
        return _sFCloudFill!!
    }

private var _sFCloudFill: ImageVector? = null
