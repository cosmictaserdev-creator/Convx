package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOctagonFill (dualtone)
 * Viewport: 25.8691 x 24.6582
 */
public val SfSymbols.Dualtone.SFOctagonFill: ImageVector
    get() {
        if (_sFOctagonFill != null) {
            return _sFOctagonFill!!
        }
        _sFOctagonFill = sfIcon(
            name = "Dualtone.SFOctagonFill",
            viewportWidth = 25.8691f,
            viewportHeight = 24.6582f
        ) {
            addSfPath("M9.08203 24.6484L16.4258 24.6484C18.1641 24.6484 18.8867 24.082 19.7559 23.1445L24.502 17.959C25.3027 17.0898 25.5078 16.5039 25.5078 15.2637L25.5078 9.38477C25.5078 8.14453 25.3027 7.56836 24.502 6.68945L19.7559 1.50391C18.8867 0.576172 18.1641 0 16.4258 0L9.08203 0C7.34375 0 6.62109 0.576172 5.76172 1.50391L1.00586 6.68945C0.205078 7.56836 0 8.14453 0 9.38477L0 15.2637C0 16.5039 0.205078 17.0898 1.00586 17.959L5.76172 23.1445C6.62109 24.082 7.34375 24.6484 9.08203 24.6484Z", fillAlpha = 0.85f)
        }
        return _sFOctagonFill!!
    }

private var _sFOctagonFill: ImageVector? = null
