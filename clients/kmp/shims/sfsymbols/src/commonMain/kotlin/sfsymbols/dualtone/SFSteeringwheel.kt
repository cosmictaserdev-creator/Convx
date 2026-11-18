package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSteeringwheel (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSteeringwheel: ImageVector
    get() {
        if (_sFSteeringwheel != null) {
            return _sFSteeringwheel!!
        }
        _sFSteeringwheel = sfIcon(
            name = "Dualtone.SFSteeringwheel",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM3.45703 7.13867C5.27344 3.96484 8.74023 1.82617 12.6953 1.82617C16.6602 1.82617 20.127 3.96484 21.9531 7.11914C22.3828 7.74414 22.1582 8.42773 21.4355 8.42773L3.97461 8.42773C3.25195 8.42773 3.02734 7.74414 3.45703 7.13867ZM12.7051 16.2402C11.1816 16.2402 9.95117 15.0195 9.95117 13.4863C9.95117 11.9629 11.1816 10.7422 12.7051 10.7422C14.2285 10.7422 15.459 11.9629 15.459 13.4863C15.459 15.0195 14.2285 16.2402 12.7051 16.2402ZM2.16797 15.4785C1.98242 14.7949 2.34375 14.4531 2.94922 14.5215C6.75781 14.8535 9.74609 18.2031 9.53125 22.0703C9.52148 22.666 9.07227 23.0371 8.4668 22.7539C5.33203 21.4258 3.00781 18.7402 2.16797 15.4785ZM23.2422 15.4785C22.4121 18.7402 20.0781 21.4258 16.9531 22.7539C16.3379 23.0371 15.8984 22.666 15.8887 22.0703C15.6738 18.2227 18.6328 14.8535 22.4707 14.5215C23.0664 14.4531 23.4375 14.7949 23.2422 15.4785Z", fillAlpha = 0.85f)
        }
        return _sFSteeringwheel!!
    }

private var _sFSteeringwheel: ImageVector? = null
