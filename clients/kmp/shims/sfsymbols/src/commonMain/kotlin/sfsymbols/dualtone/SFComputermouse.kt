package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFComputermouse (dualtone)
 * Viewport: 17.6758 x 27.1191
 */
public val SfSymbols.Dualtone.SFComputermouse: ImageVector
    get() {
        if (_sFComputermouse != null) {
            return _sFComputermouse!!
        }
        _sFComputermouse = sfIcon(
            name = "Dualtone.SFComputermouse",
            viewportWidth = 17.6758f,
            viewportHeight = 27.1191f
        ) {
            addSfPath("M8.65234 27.1191C14.3457 27.1191 17.3145 23.75 17.3145 17.3242C17.3145 13.3789 17.1582 9.87305 16.9336 7.49023C16.4648 2.22656 14.0234 0.0292969 8.65234 0.0292969C3.28125 0.0292969 0.849609 2.22656 0.371094 7.49023C0.15625 9.87305 0 13.3789 0 17.3242C0 23.75 2.96875 27.1191 8.65234 27.1191ZM8.65234 25.3906C4.08203 25.3906 1.72852 22.6465 1.72852 17.3242C1.72852 13.623 1.86523 10.1172 2.09961 7.64648C2.48047 3.30078 4.16992 1.75781 8.65234 1.75781C13.1445 1.75781 14.834 3.30078 15.2148 7.64648C15.4492 10.1172 15.5859 13.623 15.5859 17.3242C15.5859 22.6465 13.2227 25.3906 8.65234 25.3906ZM1.32812 9.16992L15.9863 9.16992L15.9863 7.73438L1.32812 7.73438ZM7.94922 8.4375L9.38477 8.44727L9.38477 1.12305L7.94922 1.11328Z", fillAlpha = 0.85f)
        }
        return _sFComputermouse!!
    }

private var _sFComputermouse: ImageVector? = null
