package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleSlashFill (dualtone)
 * Viewport: 27.8809 x 27.4977
 */
public val SfSymbols.Dualtone.SFCircleSlashFill: ImageVector
    get() {
        if (_sFCircleSlashFill != null) {
            return _sFCircleSlashFill!!
        }
        _sFCircleSlashFill = sfIcon(
            name = "Dualtone.SFCircleSlashFill",
            viewportWidth = 27.8809f,
            viewportHeight = 27.4977f
        ) {
            addSfPath("M21.0413 24.1493C18.9818 25.6062 16.4684 26.4588 13.7598 26.4588C6.74805 26.4588 1.04492 20.7459 1.04492 13.7439C1.04492 11.0335 1.89713 8.51714 3.35467 6.45523ZM26.4746 13.7439C26.4746 16.4578 25.6179 18.978 24.1528 21.0413L6.4569 3.34538C8.51996 1.87792 11.0415 1.01933 13.7598 1.01933C20.7715 1.01933 26.4746 6.73223 26.4746 13.7439Z", fillAlpha = 0.425f)
            addSfPath("M24.6973 25.8826C25.0293 26.2146 25.5664 26.2049 25.8789 25.8729C26.2109 25.5408 26.2012 25.0232 25.8789 24.701L2.79297 1.60527C2.4707 1.28301 1.94336 1.27324 1.60156 1.60527C1.28906 1.91777 1.28906 2.46465 1.60156 2.78691Z", fillAlpha = 0.85f)
        }
        return _sFCircleSlashFill!!
    }

private var _sFCircleSlashFill: ImageVector? = null
