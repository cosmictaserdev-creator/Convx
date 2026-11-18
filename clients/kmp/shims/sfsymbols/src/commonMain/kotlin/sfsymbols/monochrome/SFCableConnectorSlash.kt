package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCableConnectorSlash (monochrome)
 * Viewport: 23.7744 x 28.3301
 */
public val SfSymbols.Monochrome.SFCableConnectorSlash: ImageVector
    get() {
        if (_sFCableConnectorSlash != null) {
            return _sFCableConnectorSlash!!
        }
        _sFCableConnectorSlash = sfIcon(
            name = "Monochrome.SFCableConnectorSlash",
            viewportWidth = 23.7744f,
            viewportHeight = 28.3301f
        ) {
            addSfPath("M12.6587 18.3018L12.6587 28.3301L10.7935 28.3301L10.7935 18.2715L9.1333 18.2715C8.09814 18.2715 7.65869 17.8418 7.65869 16.8066L7.65869 13.3095ZM15.7739 7.03125L15.7739 15.2148L7.65869 7.09961L7.65869 7.03125C7.65869 5.99609 8.09814 5.55664 9.1333 5.55664L14.2993 5.55664C15.3345 5.55664 15.7739 5.99609 15.7739 7.03125Z", fillAlpha = 0.85f)
            addSfPath("M14.0942 1.46484L14.0942 4.19922L9.34814 4.19922L9.34814 1.46484C9.34814 0.429688 9.7876 0 10.8228 0L12.6099 0C13.645 0 14.0942 0.429688 14.0942 1.46484Z", fillAlpha = 0.85f)
            addSfPath("M20.6372 24.3555C20.9692 24.6777 21.4966 24.6777 21.8188 24.3555C22.1216 24.0332 22.1411 23.5059 21.8188 23.1738L2.78564 4.16016C2.47314 3.84766 1.92627 3.83789 1.604 4.16016C1.28174 4.47266 1.28174 5.01953 1.604 5.3418Z", fillAlpha = 0.85f)
        }
        return _sFCableConnectorSlash!!
    }

private var _sFCableConnectorSlash: ImageVector? = null
