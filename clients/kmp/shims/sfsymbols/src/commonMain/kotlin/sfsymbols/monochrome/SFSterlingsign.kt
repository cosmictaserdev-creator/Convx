package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSterlingsign (monochrome)
 * Viewport: 17.5781 x 22.2754
 */
public val SfSymbols.Monochrome.SFSterlingsign: ImageVector
    get() {
        if (_sFSterlingsign != null) {
            return _sFSterlingsign!!
        }
        _sFSterlingsign = sfIcon(
            name = "Monochrome.SFSterlingsign",
            viewportWidth = 17.5781f,
            viewportHeight = 22.2754f
        ) {
            addSfPath("M1.15234 22.1973L16.2891 22.1973C16.8457 22.1973 17.2656 21.8457 17.2656 21.3086C17.2656 20.7324 16.8066 20.3906 16.2891 20.3906L3.65234 20.3906L3.65234 20.3027C5.75195 19.6875 6.95312 17.2266 6.95312 14.8633C6.95312 13.75 6.77734 12.8711 6.5625 12.0117L13.5352 12.0117C14.0332 12.0117 14.4043 11.6895 14.4043 11.2109C14.4043 10.7227 14.0332 10.4004 13.5352 10.4004L6.12305 10.4004C5.81055 9.375 5.52734 8.20312 5.52734 6.82617C5.52734 3.62305 7.90039 1.86523 11.3281 1.86523C12.4219 1.86523 13.6133 2.03125 14.4922 2.36328C14.8047 2.46094 15.0586 2.5293 15.2832 2.5293C15.8008 2.5293 16.123 2.22656 16.123 1.70898C16.123 1.18164 15.752 0.908203 15.2539 0.693359C13.9258 0.146484 12.5684 0 11.1621 0C6.68945 0 3.36914 2.36328 3.36914 6.94336C3.36914 8.07617 3.60352 9.10156 3.95508 10.4004L1.08398 10.4004C0.595703 10.4004 0.224609 10.7227 0.224609 11.2109C0.224609 11.709 0.615234 12.0117 1.08398 12.0117L4.375 12.0117C4.74609 13.2812 4.86328 14.1211 4.86328 14.9512C4.86328 17.3145 3.50586 19.5117 0.888672 20.1953C0.361328 20.3613 0 20.6738 0 21.2695C0 21.8359 0.449219 22.1973 1.15234 22.1973Z", fillAlpha = 0.85f)
        }
        return _sFSterlingsign!!
    }

private var _sFSterlingsign: ImageVector? = null
