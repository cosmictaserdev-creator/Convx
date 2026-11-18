package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBahtsign (monochrome)
 * Viewport: 15.5078 x 28.3691
 */
public val SfSymbols.Monochrome.SFBahtsign: ImageVector
    get() {
        if (_sFBahtsign != null) {
            return _sFBahtsign!!
        }
        _sFBahtsign = sfIcon(
            name = "Monochrome.SFBahtsign",
            viewportWidth = 15.5078f,
            viewportHeight = 28.3691f
        ) {
            addSfPath("M1.33789 25.4102L7.58789 25.4102C12.002 25.4102 15.1465 23.3398 15.1465 19.3457C15.1465 16.3672 13.0957 14.1797 9.85352 13.8086L9.85352 13.7012C12.3438 13.252 13.9941 11.084 13.9941 8.67188C13.9941 5.20508 11.1719 3.19336 7.5293 3.19336L1.33789 3.19336C0.595703 3.19336 0 3.61328 0 4.56055L0 24.0625C0 24.9414 0.556641 25.4102 1.33789 25.4102ZM2.08008 5.01953L7.07031 5.01953C9.87305 5.01953 11.8945 6.30859 11.8945 8.87695C11.8945 11.7383 9.57031 13.0469 6.50391 13.0469L2.08008 13.0469ZM2.08008 23.584L2.08008 14.873L6.98242 14.873C10.4785 14.873 13.0371 16.1914 13.0371 19.248C13.0371 22.2168 10.6152 23.584 7.27539 23.584ZM6.38672 28.3691C6.80664 28.3691 7.08984 28.0859 7.08984 27.6562L7.08984 0.878906C7.08984 0.439453 6.80664 0.146484 6.38672 0.146484C5.95703 0.146484 5.66406 0.439453 5.66406 0.878906L5.66406 27.6562C5.66406 28.0859 5.95703 28.3691 6.38672 28.3691Z", fillAlpha = 0.85f)
        }
        return _sFBahtsign!!
    }

private var _sFBahtsign: ImageVector? = null
