package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRhombusFill (dualtone)
 * Viewport: 19.5703 x 25.4004
 */
public val SfSymbols.Dualtone.SFRhombusFill: ImageVector
    get() {
        if (_sFRhombusFill != null) {
            return _sFRhombusFill!!
        }
        _sFRhombusFill = sfIcon(
            name = "Dualtone.SFRhombusFill",
            viewportWidth = 19.5703f,
            viewportHeight = 25.4004f
        ) {
            addSfPath("M9.59961 25.3809C10.2344 25.3809 10.5469 24.9512 11.1621 24.1211L18.7207 13.9844C19.0332 13.5645 19.209 13.1445 19.209 12.6953C19.209 12.2363 19.0332 11.8262 18.7207 11.3965L11.1621 1.25977C10.5469 0.429688 10.2344 0 9.59961 0C8.97461 0 8.66211 0.429688 8.03711 1.25977L0.488281 11.3965C0.166016 11.8262 0 12.2363 0 12.6953C0 13.1445 0.166016 13.5645 0.488281 13.9844L8.03711 24.1211C8.66211 24.9512 8.97461 25.3809 9.59961 25.3809Z", fillAlpha = 0.85f)
        }
        return _sFRhombusFill!!
    }

private var _sFRhombusFill: ImageVector? = null
