package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesign (monochrome)
 * Viewport: 17.5586 x 22.9395
 */
public val SfSymbols.Monochrome.SFTengesign: ImageVector
    get() {
        if (_sFTengesign != null) {
            return _sFTengesign!!
        }
        _sFTengesign = sfIcon(
            name = "Monochrome.SFTengesign",
            viewportWidth = 17.5586f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M8.63281 22.9102C9.27734 22.9102 9.6875 22.4902 9.6875 21.8164L9.6875 6.36719L16.2891 6.36719C16.8359 6.36719 17.1973 6.01562 17.1973 5.47852C17.1973 4.96094 16.8359 4.61914 16.2891 4.61914L0.957031 4.61914C0.410156 4.61914 0.0488281 4.96094 0.0488281 5.47852C0.0488281 6.01562 0.410156 6.36719 0.957031 6.36719L7.56836 6.36719L7.56836 21.8164C7.56836 22.4902 7.98828 22.9102 8.63281 22.9102ZM0 0.712891C0 1.13281 0.302734 1.42578 0.722656 1.42578L16.4844 1.42578C16.8945 1.42578 17.1973 1.12305 17.1973 0.712891C17.1973 0.3125 16.8945 0 16.4844 0L0.722656 0C0.302734 0 0 0.3125 0 0.712891Z", fillAlpha = 0.85f)
        }
        return _sFTengesign!!
    }

private var _sFTengesign: ImageVector? = null
