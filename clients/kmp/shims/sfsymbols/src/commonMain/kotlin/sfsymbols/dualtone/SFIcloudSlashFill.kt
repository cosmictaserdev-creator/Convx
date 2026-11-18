package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudSlashFill (dualtone)
 * Viewport: 31.5625 x 29.3457
 */
public val SfSymbols.Dualtone.SFIcloudSlashFill: ImageVector
    get() {
        if (_sFIcloudSlashFill != null) {
            return _sFIcloudSlashFill!!
        }
        _sFIcloudSlashFill = sfIcon(
            name = "Dualtone.SFIcloudSlashFill",
            viewportWidth = 31.5625f,
            viewportHeight = 29.3457f
        ) {
            addSfPath("M20.3843 24.4703L7.37305 24.4629C3.16406 24.4629 0 21.4648 0 17.8223C0 14.7852 1.78711 12.334 4.58984 11.9434C4.60048 10.9915 4.85722 10.1251 5.29295 9.39283ZM27.2559 12.5781C29.7363 13.5547 31.2012 15.8203 31.2012 18.4082C31.2012 21.2212 29.191 23.5536 26.3679 24.25L9.01044 6.90858C9.67354 6.81656 10.362 6.87192 11.0352 7.08984C12.4805 4.85352 14.873 2.99805 18.3008 2.99805C23.3594 2.99805 27.3242 7.03125 27.2559 12.5781Z", fillAlpha = 0.425f)
            addSfPath("M25.5762 27.7441C25.9082 28.0664 26.4355 28.0664 26.7578 27.7441C27.0801 27.4219 27.0801 26.8945 26.7578 26.5625L5.61523 5.42969C5.29297 5.10742 4.74609 5.11719 4.42383 5.42969C4.11133 5.74219 4.12109 6.29883 4.42383 6.61133Z", fillAlpha = 0.85f)
        }
        return _sFIcloudSlashFill!!
    }

private var _sFIcloudSlashFill: ImageVector? = null
