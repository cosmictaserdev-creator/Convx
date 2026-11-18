package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleDown2Fill (dualtone)
 * Viewport: 15.9668 x 27.4121
 */
public val SfSymbols.Dualtone.SFArrowtriangleDown2Fill: ImageVector
    get() {
        if (_sFArrowtriangleDown2Fill != null) {
            return _sFArrowtriangleDown2Fill!!
        }
        _sFArrowtriangleDown2Fill = sfIcon(
            name = "Dualtone.SFArrowtriangleDown2Fill",
            viewportWidth = 15.9668f,
            viewportHeight = 27.4121f
        ) {
            addSfPath("M1.41602 13.5742C0.458984 13.5742 0 14.1504 0 14.8145C0 15.1074 0.0976562 15.4199 0.244141 15.6836L6.5332 26.3965C6.94336 27.0996 7.27539 27.4121 7.80273 27.4121C8.33008 27.4121 8.66211 27.0996 9.0625 26.3965L15.3613 15.6836C15.5078 15.4199 15.6055 15.1074 15.6055 14.8145C15.6055 14.1504 15.1465 13.5742 14.1797 13.5742Z", fillAlpha = 0.85f)
            addSfPath("M1.41602 0.0390625C0.458984 0.0390625 0 0.615234 0 1.2793C0 1.57227 0.0976562 1.88477 0.244141 2.14844L6.5332 12.8613C6.94336 13.5645 7.27539 13.877 7.80273 13.877C8.33008 13.877 8.65234 13.5645 9.0625 12.8613L15.3613 2.14844C15.5078 1.88477 15.6055 1.57227 15.6055 1.2793C15.6055 0.615234 15.1465 0.0390625 14.1797 0.0390625Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleDown2Fill!!
    }

private var _sFArrowtriangleDown2Fill: ImageVector? = null
