package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLockCircleFill: ImageVector
    get() {
        if (_sFLockCircleFill != null) {
            return _sFLockCircleFill!!
        }
        _sFLockCircleFill = sfIcon(
            name = "Monochrome.SFLockCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.23828 9.64844L9.23828 11.25C8.4375 11.2891 8.03711 11.7383 8.03711 12.6855L8.03711 17.8809C8.03711 18.8672 8.47656 19.3164 9.38477 19.3164L16.0449 19.3164C16.9531 19.3164 17.3926 18.8672 17.3926 17.8809L17.3926 12.6855C17.3926 11.7383 16.9922 11.2891 16.1914 11.25L16.1914 9.64844C16.1914 7.32422 14.7852 5.77148 12.7148 5.77148C10.6445 5.77148 9.23828 7.32422 9.23828 9.64844ZM14.9902 9.52148L14.9902 11.2402L10.4492 11.2402L10.4492 9.52148C10.4492 7.97852 11.3574 6.94336 12.7148 6.94336C14.0723 6.94336 14.9902 7.97852 14.9902 9.52148Z", fillAlpha = 0.85f)
        }
        return _sFLockCircleFill!!
    }

private var _sFLockCircleFill: ImageVector? = null
