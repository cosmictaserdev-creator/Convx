package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHourglass (monochrome)
 * Viewport: 14.9707 x 22.9199
 */
public val SfSymbols.Monochrome.SFHourglass: ImageVector
    get() {
        if (_sFHourglass != null) {
            return _sFHourglass!!
        }
        _sFHourglass = sfIcon(
            name = "Monochrome.SFHourglass",
            viewportWidth = 14.9707f,
            viewportHeight = 22.9199f
        ) {
            addSfPath("M14.6094 1.94336L14.6094 2.20703C14.6094 5.17578 11.3867 8.45703 9.51172 10.3223C8.80859 11.0254 8.75977 11.7969 9.51172 12.5488C11.3867 14.4141 14.6094 17.7148 14.6094 20.6641L14.6094 20.957C14.6094 22.207 13.8672 22.9102 12.6758 22.9102L1.93359 22.9102C0.742188 22.9102 0 22.207 0 20.957L0 20.6641C0 17.7148 3.21289 14.4141 5.08789 12.5488C5.83984 11.7969 5.79102 11.0254 5.08789 10.3223C3.21289 8.45703 0 5.17578 0 2.20703L0 1.94336C0 0.693359 0.742188 0 1.93359 0L12.6758 0C13.8672 0 14.6094 0.693359 14.6094 1.94336ZM3.69141 6.09375C3.33984 6.09375 3.18359 6.44531 3.47656 6.75781C4.28711 7.64648 5.49805 8.75 6.43555 9.56055C6.80664 9.87305 6.89453 10.127 6.89453 10.625L6.89453 16.0449C6.89453 16.25 6.8457 16.3184 6.68945 16.4648L2.55859 20.459C2.2168 20.7812 2.35352 21.3477 2.8418 21.3477L11.7676 21.3477C12.2559 21.3477 12.3828 20.7812 12.041 20.459L7.91992 16.4648C7.76367 16.3184 7.71484 16.25 7.71484 16.0449L7.71484 10.625C7.71484 10.127 7.80273 9.87305 8.17383 9.56055C9.10156 8.75 10.3223 7.64648 11.1328 6.75781C11.4258 6.44531 11.2695 6.09375 10.9082 6.09375Z", fillAlpha = 0.85f)
        }
        return _sFHourglass!!
    }

private var _sFHourglass: ImageVector? = null
