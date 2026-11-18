package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMultiply (dualtone)
 * Viewport: 16.897 x 16.5698
 */
public val SfSymbols.Dualtone.SFMultiply: ImageVector
    get() {
        if (_sFMultiply != null) {
            return _sFMultiply!!
        }
        _sFMultiply = sfIcon(
            name = "Dualtone.SFMultiply",
            viewportWidth = 16.897f,
            viewportHeight = 16.5698f
        ) {
            addSfPath("M1.63209 16.2879L16.261 1.65895C16.6223 1.29762 16.6321 0.672619 16.261 0.311291C15.8899-0.0695682 15.2747-0.0598026 14.9133 0.311291L0.27467 14.9402C-0.0866577 15.3015-0.0964233 15.9265 0.27467 16.2879C0.645764 16.6687 1.261 16.6589 1.63209 16.2879ZM0.27467 1.65895L14.9133 16.2879C15.2747 16.6589 15.8899 16.6687 16.261 16.2879C16.6321 15.9265 16.6223 15.3015 16.261 14.9402L1.63209 0.311291C1.261-0.0598026 0.645764-0.0695682 0.27467 0.311291C-0.0964233 0.672619-0.0866577 1.29762 0.27467 1.65895Z", fillAlpha = 0.85f)
        }
        return _sFMultiply!!
    }

private var _sFMultiply: ImageVector? = null
