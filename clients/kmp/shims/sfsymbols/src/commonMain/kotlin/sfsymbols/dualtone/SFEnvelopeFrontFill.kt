package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeFrontFill (dualtone)
 * Viewport: 30.498 x 21.4551
 */
public val SfSymbols.Dualtone.SFEnvelopeFrontFill: ImageVector
    get() {
        if (_sFEnvelopeFrontFill != null) {
            return _sFEnvelopeFrontFill!!
        }
        _sFEnvelopeFrontFill = sfIcon(
            name = "Dualtone.SFEnvelopeFrontFill",
            viewportWidth = 30.498f,
            viewportHeight = 21.4551f
        ) {
            addSfPath("M30.1367 3.76953L30.1367 17.6855C30.1367 20.1758 28.8477 21.4551 26.6309 21.4551L3.79883 21.4551C1.29883 21.4551 0 20.1758 0 17.6855L0 3.76953C0 1.2793 1.28906 0.00976562 3.50586 0.00976562L26.3281 0.00976562C28.8379 0.00976562 30.1367 1.2793 30.1367 3.76953ZM10.1465 14.9512C9.75586 14.9512 9.43359 15.2637 9.43359 15.6445C9.43359 16.0352 9.75586 16.3477 10.1465 16.3477L17.0508 16.3477C17.4414 16.3477 17.7637 16.0352 17.7637 15.6445C17.7637 15.2637 17.4414 14.9512 17.0508 14.9512ZM10.1465 11.6211C9.75586 11.6211 9.43359 11.9336 9.43359 12.3242C9.43359 12.7148 9.75586 13.0273 10.1465 13.0273L19.8535 13.0273C20.2441 13.0273 20.5566 12.7148 20.5566 12.3242C20.5566 11.9336 20.2441 11.6211 19.8535 11.6211ZM22.2852 3.62305C21.709 3.62305 21.2793 4.05273 21.2793 4.62891L21.2793 7.02148C21.2793 7.58789 21.709 8.01758 22.2852 8.01758L25.5664 8.01758C26.1426 8.01758 26.5723 7.58789 26.5723 7.02148L26.5723 4.62891C26.5723 4.05273 26.1426 3.62305 25.5664 3.62305Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeFrontFill!!
    }

private var _sFEnvelopeFrontFill: ImageVector? = null
