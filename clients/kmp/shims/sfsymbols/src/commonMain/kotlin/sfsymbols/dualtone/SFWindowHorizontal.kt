package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindowHorizontal (dualtone)
 * Viewport: 32.168 x 20.6543
 */
public val SfSymbols.Dualtone.SFWindowHorizontal: ImageVector
    get() {
        if (_sFWindowHorizontal != null) {
            return _sFWindowHorizontal!!
        }
        _sFWindowHorizontal = sfIcon(
            name = "Dualtone.SFWindowHorizontal",
            viewportWidth = 32.168f,
            viewportHeight = 20.6543f
        ) {
            addSfPath("M3.31055 9.39453L3.31055 2.08008C3.31055 1.86523 3.4375 1.72852 3.62305 1.72852L15.1562 1.72852L15.1562 9.39453ZM16.582 9.39453L16.582 1.72852L28.1152 1.72852C28.3008 1.72852 28.4375 1.86523 28.4375 2.08008L28.4375 9.39453Z", fillAlpha = 0.2125f)
            addSfPath("M0 19.7168C0 20.2246 0.419922 20.6348 0.927734 20.6348L30.8789 20.6348C31.3867 20.6348 31.8066 20.2246 31.8066 19.7168C31.8066 19.1992 31.3867 18.7891 30.8789 18.7891L30.166 18.7891L30.166 1.66016C30.166 0.673828 29.4824 0 28.4473 0L3.29102 0C2.25586 0 1.57227 0.673828 1.57227 1.66016L1.57227 18.7891L0.927734 18.7891C0.419922 18.7891 0 19.1992 0 19.7168ZM3.31055 9.39453L3.31055 2.08008C3.31055 1.86523 3.4375 1.72852 3.62305 1.72852L15.1562 1.72852L15.1562 9.39453ZM16.582 9.39453L16.582 1.72852L28.1152 1.72852C28.3008 1.72852 28.4375 1.86523 28.4375 2.08008L28.4375 9.39453ZM3.31055 18.7891L3.31055 11.25L28.4375 11.25L28.4375 18.7891Z", fillAlpha = 0.85f)
        }
        return _sFWindowHorizontal!!
    }

private var _sFWindowHorizontal: ImageVector? = null
