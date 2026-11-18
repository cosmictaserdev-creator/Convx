package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFrancsign (monochrome)
 * Viewport: 16.5625 x 22.6855
 */
public val SfSymbols.Monochrome.SFFrancsign: ImageVector
    get() {
        if (_sFFrancsign != null) {
            return _sFFrancsign!!
        }
        _sFFrancsign = sfIcon(
            name = "Monochrome.SFFrancsign",
            viewportWidth = 16.5625f,
            viewportHeight = 22.6855f
        ) {
            addSfPath("M4.18945 22.6465C4.80469 22.6465 5.24414 22.1875 5.24414 21.5625L5.24414 18.3008L11.1035 18.3008C11.5039 18.3008 11.7969 17.998 11.7969 17.5781C11.7969 17.168 11.5039 16.8652 11.1035 16.8652L5.24414 16.8652L5.24414 11.9141L14.3066 11.9141C14.873 11.9141 15.2734 11.5137 15.2734 10.9668C15.2734 10.4199 14.873 10.0293 14.3066 10.0293L5.24414 10.0293L5.24414 1.91406L15.2246 1.91406C15.791 1.91406 16.2012 1.52344 16.2012 0.966797C16.2012 0.400391 15.791 0 15.2246 0L4.18945 0C3.53516 0 3.11523 0.449219 3.11523 1.10352L3.11523 16.8652L0.703125 16.8652C0.292969 16.8652 0 17.168 0 17.5781C0 17.998 0.292969 18.3008 0.703125 18.3008L3.11523 18.3008L3.11523 21.5625C3.11523 22.1777 3.53516 22.6465 4.18945 22.6465Z", fillAlpha = 0.85f)
        }
        return _sFFrancsign!!
    }

private var _sFFrancsign: ImageVector? = null
