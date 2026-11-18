package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLockCircleFill: ImageVector
    get() {
        if (_sFLockCircleFill != null) {
            return _sFLockCircleFill!!
        }
        _sFLockCircleFill = sfIcon(
            name = "Dualtone.SFLockCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.03711 17.8809L8.03711 12.6855C8.03711 11.7383 8.4375 11.2891 9.23828 11.25L9.23828 9.64844C9.23828 7.32422 10.6445 5.77148 12.7148 5.77148C14.7852 5.77148 16.1914 7.32422 16.1914 9.64844L16.1914 11.25C16.9922 11.2891 17.3926 11.7383 17.3926 12.6855L17.3926 17.8809C17.3926 18.8672 16.9531 19.3164 16.0449 19.3164L9.38477 19.3164C8.47656 19.3164 8.03711 18.8672 8.03711 17.8809ZM10.4492 11.2402L14.9902 11.2402L14.9902 9.52148C14.9902 7.97852 14.0723 6.94336 12.7148 6.94336C11.3574 6.94336 10.4492 7.97852 10.4492 9.52148Z", fillAlpha = 0.85f)
        }
        return _sFLockCircleFill!!
    }

private var _sFLockCircleFill: ImageVector? = null
