package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBolt (dualtone)
 * Viewport: 17.666 x 27.9091
 */
public val SfSymbols.Dualtone.SFBolt: ImageVector
    get() {
        if (_sFBolt != null) {
            return _sFBolt!!
        }
        _sFBolt = sfIcon(
            name = "Dualtone.SFBolt",
            viewportWidth = 17.666f,
            viewportHeight = 27.9091f
        ) {
            addSfPath("M5.61523 27.2407L17.002 12.6216C17.1973 12.3774 17.3047 12.1528 17.3047 11.8989C17.3047 11.4985 17.002 11.186 16.543 11.186L9.375 11.186L13.1738 1.26413C13.6035 0.121555 12.4121-0.474148 11.6895 0.463352L0.302734 15.0825C0.107422 15.3266 0 15.5415 0 15.7954C0 16.2055 0.302734 16.518 0.771484 16.518L7.92969 16.518L4.13086 26.4399C3.70117 27.5727 4.89258 28.1684 5.61523 27.2407ZM6.2793 24.3012L10.4004 14.9067L2.69531 14.9067L11.3184 3.56882L11.0156 3.40281L6.89453 12.7973L14.5996 12.7973L5.97656 24.1352Z", fillAlpha = 0.85f)
        }
        return _sFBolt!!
    }

private var _sFBolt: ImageVector? = null
