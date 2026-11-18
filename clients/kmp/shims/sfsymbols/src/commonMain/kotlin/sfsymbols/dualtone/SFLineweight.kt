package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLineweight (dualtone)
 * Viewport: 21.6992 x 21.0645
 */
public val SfSymbols.Dualtone.SFLineweight: ImageVector
    get() {
        if (_sFLineweight != null) {
            return _sFLineweight!!
        }
        _sFLineweight = sfIcon(
            name = "Dualtone.SFLineweight",
            viewportWidth = 21.6992f,
            viewportHeight = 21.0645f
        ) {
            addSfPath("M0.566406 2.23633L20.7715 2.23633C21.123 2.23633 21.3379 2.02148 21.3379 1.67969L21.3379 1.16211C21.3379 0.820312 21.123 0.605469 20.7715 0.605469L0.566406 0.605469C0.214844 0.605469 0 0.820312 0 1.16211L0 1.67969C0 2.02148 0.214844 2.23633 0.566406 2.23633ZM0.9375 10.2734L20.4102 10.2734C20.9863 10.2734 21.3379 9.93164 21.3379 9.3457L21.3379 7.35352C21.3379 6.76758 20.9863 6.42578 20.4102 6.42578L0.9375 6.42578C0.351562 6.42578 0 6.76758 0 7.35352L0 9.3457C0 9.93164 0.351562 10.2734 0.9375 10.2734ZM0.9375 21.0645L20.4102 21.0645C20.9863 21.0645 21.3379 20.7227 21.3379 20.1367L21.3379 15.2539C21.3379 14.668 20.9863 14.3262 20.4102 14.3262L0.9375 14.3262C0.351562 14.3262 0 14.668 0 15.2539L0 20.1367C0 20.7227 0.351562 21.0645 0.9375 21.0645Z", fillAlpha = 0.85f)
        }
        return _sFLineweight!!
    }

private var _sFLineweight: ImageVector? = null
