package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpLeftBottomrightRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowUpLeftBottomrightRectangleFill: ImageVector
    get() {
        if (_sFArrowUpLeftBottomrightRectangleFill != null) {
            return _sFArrowUpLeftBottomrightRectangleFill!!
        }
        _sFArrowUpLeftBottomrightRectangleFill = sfIcon(
            name = "Dualtone.SFArrowUpLeftBottomrightRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M25.7715 17.9785L21.4062 13.6133L19.1895 11.5039C18.3691 10.7422 17.4023 11.7969 18.1348 12.5879L20.2246 14.7852L24.5801 19.1504C24.7461 19.3066 24.9512 19.4043 25.2246 19.4043C25.7227 19.4043 26.0449 19.082 26.0449 18.5742C26.0449 18.3496 25.9375 18.1348 25.7715 17.9785ZM17.9297 10.4004C17.3242 10.4004 17.0215 10.7129 17.0215 11.3184L17.0215 17.334C17.0312 17.8613 17.3438 18.1836 17.832 18.1836C18.3301 18.1836 18.6523 17.8516 18.6621 17.3047L18.6621 15.0391L18.3496 11.7285L21.4355 12.041L23.916 12.041C24.4629 12.0312 24.8047 11.709 24.8047 11.2207C24.8047 10.7227 24.4727 10.4102 23.9453 10.4004Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpLeftBottomrightRectangleFill!!
    }

private var _sFArrowUpLeftBottomrightRectangleFill: ImageVector? = null
