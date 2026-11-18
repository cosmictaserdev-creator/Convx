package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneBadgeLocation (dualtone)
 * Viewport: 23.4683 x 38.9552
 */
public val SfSymbols.Dualtone.SFIphoneBadgeLocation: ImageVector
    get() {
        if (_sFIphoneBadgeLocation != null) {
            return _sFIphoneBadgeLocation!!
        }
        _sFIphoneBadgeLocation = sfIcon(
            name = "Dualtone.SFIphoneBadgeLocation",
            viewportWidth = 23.4683f,
            viewportHeight = 38.9552f
        ) {
            addSfPath("M19.3855 9.58015L19.3855 24.2098L17.6473 25.0311L17.6473 9.83406C17.6473 8.69148 17.0223 8.08601 15.8406 8.08601L7.2664 8.08601C6.07499 8.08601 5.44999 8.69148 5.44999 9.83406L5.44999 29.1016C5.44999 30.2442 6.07499 30.8497 7.2664 30.8497L8.15213 30.8497C8.12999 31.494 8.38417 32.1396 8.88136 32.5782L7.05155 32.5782C5.01054 32.5782 3.72148 31.3282 3.72148 29.3555L3.72148 9.58015C3.72148 7.6075 5.01054 6.3575 7.05155 6.3575L16.0457 6.3575C18.0867 6.3575 19.3855 7.6075 19.3855 9.58015ZM13.8484 9.72664C13.8484 10.1856 13.4969 10.5372 13.0476 10.5372L10.0691 10.5372C9.61015 10.5372 9.25859 10.1856 9.25859 9.72664C9.25859 9.28718 9.61015 8.93562 10.0691 8.93562L13.0476 8.93562C13.4969 8.93562 13.8484 9.28718 13.8484 9.72664Z", fillAlpha = 0.425f)
            addSfPath("M10.4402 31.5626L15.2644 31.5626C15.45 31.5626 15.5672 31.6895 15.5672 31.8751L15.5769 36.6407C15.5769 37.5391 16.5242 37.627 16.8172 36.9825L21.8074 26.3477C22.1883 25.5665 21.5242 24.9219 20.743 25.293L10.118 30.3126C9.47343 30.6348 9.59062 31.5626 10.4402 31.5626Z", fillAlpha = 0.85f)
        }
        return _sFIphoneBadgeLocation!!
    }

private var _sFIphoneBadgeLocation: ImageVector? = null
