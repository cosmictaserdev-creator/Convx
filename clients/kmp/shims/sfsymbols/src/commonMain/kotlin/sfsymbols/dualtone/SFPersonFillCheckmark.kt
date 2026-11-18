package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillCheckmark (dualtone)
 * Viewport: 31.2207 x 22.5293
 */
public val SfSymbols.Dualtone.SFPersonFillCheckmark: ImageVector
    get() {
        if (_sFPersonFillCheckmark != null) {
            return _sFPersonFillCheckmark!!
        }
        _sFPersonFillCheckmark = sfIcon(
            name = "Dualtone.SFPersonFillCheckmark",
            viewportWidth = 31.2207f,
            viewportHeight = 22.5293f
        ) {
            addSfPath("M2.06055 22.5195L19.0723 22.5195C20.3516 22.5195 21.1328 21.9043 21.1328 20.9082C21.1328 17.666 17.0801 13.1934 10.5664 13.1934C4.05273 13.1934 0 17.666 0 20.9082C0 21.9043 0.78125 22.5195 2.06055 22.5195ZM10.5762 10.9375C13.3008 10.9375 15.5859 8.51562 15.5859 5.39062C15.5859 2.33398 13.2812 0 10.5762 0C7.88086 0 5.57617 2.37305 5.57617 5.41016C5.57617 8.51562 7.86133 10.9375 10.5762 10.9375Z", fillAlpha = 0.425f)
            addSfPath("M23.7598 11.6309C24.1113 11.6309 24.4043 11.4551 24.6191 11.1328L30.6152 1.74805C30.7324 1.55273 30.8594 1.29883 30.8594 1.07422C30.8594 0.595703 30.4395 0.273438 29.9902 0.273438C29.707 0.273438 29.4336 0.439453 29.2383 0.761719L23.7109 9.54102L20.957 6.09375C20.7129 5.76172 20.4688 5.66406 20.166 5.66406C19.6973 5.66406 19.3359 6.03516 19.3359 6.51367C19.3359 6.74805 19.4238 6.99219 19.5801 7.19727L22.8418 11.1328C23.1152 11.4844 23.3984 11.6309 23.7598 11.6309Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillCheckmark!!
    }

private var _sFPersonFillCheckmark: ImageVector? = null
