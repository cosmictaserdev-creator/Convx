package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeFill (dualtone)
 * Viewport: 30.498 x 21.4551
 */
public val SfSymbols.Dualtone.SFEnvelopeFill: ImageVector
    get() {
        if (_sFEnvelopeFill != null) {
            return _sFEnvelopeFill!!
        }
        _sFEnvelopeFill = sfIcon(
            name = "Dualtone.SFEnvelopeFill",
            viewportWidth = 30.498f,
            viewportHeight = 21.4551f
        ) {
            addSfPath("M15.0684 12.2656C15.4883 12.2656 15.8984 12.0996 16.3184 11.7188L28.8086 0.664062C28.1641 0.214844 27.3828 0.00976562 26.3281 0.00976562L3.79883 0.00976562C2.74414 0.00976562 1.97266 0.214844 1.31836 0.664062L13.8184 11.7188C14.2383 12.0996 14.6387 12.2656 15.0684 12.2656ZM0.390625 19.6094L9.75586 10.2539L0.351562 1.93359C0.166016 2.23633 0 2.90039 0 3.76953L0 17.6855C0 18.5156 0.146484 19.1309 0.390625 19.6094ZM3.50586 21.4551L26.6309 21.4551C27.5879 21.4551 28.2812 21.2207 28.75 20.8887L19.1699 11.3086L17.2852 12.9785C16.5723 13.6035 15.8301 13.916 15.0684 13.916C14.2969 13.916 13.5645 13.6035 12.8516 12.9785L10.9668 11.3086L1.37695 20.8887C1.8457 21.2207 2.54883 21.4551 3.50586 21.4551ZM29.7461 19.6094C29.9902 19.1309 30.1367 18.5156 30.1367 17.6855L30.1367 3.76953C30.1367 2.90039 29.9609 2.23633 29.7754 1.93359L20.3711 10.2539Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeFill!!
    }

private var _sFEnvelopeFill: ImageVector? = null
