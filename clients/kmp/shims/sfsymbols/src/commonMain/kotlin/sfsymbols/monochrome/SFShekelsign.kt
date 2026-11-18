package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShekelsign (monochrome)
 * Viewport: 20.791 x 22.373
 */
public val SfSymbols.Monochrome.SFShekelsign: ImageVector
    get() {
        if (_sFShekelsign != null) {
            return _sFShekelsign!!
        }
        _sFShekelsign = sfIcon(
            name = "Monochrome.SFShekelsign",
            viewportWidth = 20.791f,
            viewportHeight = 22.373f
        ) {
            addSfPath("M0.966797 22.3633C1.5625 22.3633 1.9043 21.8945 1.9043 21.3672L1.9043 2.26562L8.04688 2.26562C11.1914 2.26562 12.3633 3.35938 12.3633 6.58203L12.3633 14.9219C12.3633 15.5371 12.7246 15.918 13.3301 15.918C13.9062 15.918 14.2578 15.5371 14.2578 14.9219L14.2578 6.20117C14.2578 2.1582 12.666 0.185547 8.67188 0.185547L1.34766 0.185547C0.537109 0.185547 0 0.703125 0 1.5332L0 21.3672C0 21.9531 0.380859 22.3633 0.966797 22.3633ZM7.50977 22.1582L14.8047 22.1582C19.0234 22.1582 20.4297 19.9316 20.4297 15.7324L20.4297 0.976562C20.4297 0.371094 20.0781 0 19.5117 0C18.9062 0 18.5449 0.371094 18.5449 0.976562L18.5449 15.7227C18.5449 19.0137 17.6758 20.0781 14.3066 20.0781L8.07617 20.0781L8.07617 7.35352C8.07617 6.73828 7.71484 6.35742 7.14844 6.35742C6.54297 6.35742 6.18164 6.73828 6.18164 7.35352L6.18164 20.8105C6.18164 21.6113 6.70898 22.1582 7.50977 22.1582Z", fillAlpha = 0.85f)
        }
        return _sFShekelsign!!
    }

private var _sFShekelsign: ImageVector? = null
