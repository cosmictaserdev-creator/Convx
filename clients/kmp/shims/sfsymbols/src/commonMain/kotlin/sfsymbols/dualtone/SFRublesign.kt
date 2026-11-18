package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRublesign (dualtone)
 * Viewport: 17.9395 x 22.666
 */
public val SfSymbols.Dualtone.SFRublesign: ImageVector
    get() {
        if (_sFRublesign != null) {
            return _sFRublesign!!
        }
        _sFRublesign = sfIcon(
            name = "Dualtone.SFRublesign",
            viewportWidth = 17.9395f,
            viewportHeight = 22.666f
        ) {
            addSfPath("M4.48242 22.6465C5.12695 22.6465 5.53711 22.2266 5.53711 21.5625L5.53711 18.418L11.8652 18.418C12.2949 18.418 12.5586 18.1445 12.5586 17.7148C12.5586 17.2754 12.2949 16.9824 11.8652 16.9824L5.53711 16.9824L5.53711 13.584L10.4004 13.584C14.6191 13.584 17.5781 10.9863 17.5781 6.79688C17.5781 2.58789 14.6191 0 10.4199 0L4.48242 0C3.82812 0 3.41797 0.419922 3.41797 1.10352L3.41797 12.1484L0.703125 12.1484C0.273438 12.1484 0 12.4316 0 12.8613C0 13.3008 0.273438 13.584 0.703125 13.584L3.41797 13.584L3.41797 16.9824L0.703125 16.9824C0.273438 16.9824 0.00976562 17.2754 0.00976562 17.7148C0.00976562 18.1445 0.273438 18.418 0.703125 18.418L3.41797 18.418L3.41797 21.5625C3.41797 22.2266 3.82812 22.6465 4.48242 22.6465ZM5.53711 11.748L5.53711 1.86523L9.90234 1.86523C13.2812 1.86523 15.4004 3.54492 15.4004 6.79688C15.4004 10.0586 13.2812 11.748 9.90234 11.748Z", fillAlpha = 0.85f)
        }
        return _sFRublesign!!
    }

private var _sFRublesign: ImageVector? = null
