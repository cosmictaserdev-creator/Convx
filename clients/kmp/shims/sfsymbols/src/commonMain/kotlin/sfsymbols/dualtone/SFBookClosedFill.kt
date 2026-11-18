package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookClosedFill (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFBookClosedFill: ImageVector
    get() {
        if (_sFBookClosedFill != null) {
            return _sFBookClosedFill!!
        }
        _sFBookClosedFill = sfIcon(
            name = "Dualtone.SFBookClosedFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M3.70117 26.9238L20.1074 26.9238C20.5859 26.9238 20.9668 26.543 20.9668 26.0547C20.9668 25.6934 20.7129 25.3613 20.3809 25.2344C18.7207 24.4727 18.3594 22.3828 19.9512 20.7324C20.4395 20.2344 20.9668 19.5703 20.9668 18.3496L20.9668 3.81836C20.9668 1.29883 19.707 0.0195312 17.207 0.0195312L3.75977 0.0195312C1.25977 0.0195312 0 1.28906 0 3.81836L0 23.2324C0 25.6641 1.26953 26.9238 3.70117 26.9238ZM3.78906 25.1953C2.46094 25.1953 1.72852 24.4727 1.72852 23.2129C1.72852 22.0215 2.55859 21.2695 3.87695 21.2695L16.9922 21.2695C17.2363 21.2695 17.4414 21.2402 17.6367 21.1914C16.9434 22.5977 17.0898 24.082 17.9492 25.1953ZM3.37891 19.4824C3.03711 19.4824 2.74414 19.1992 2.74414 18.8477L2.74414 2.49023C2.74414 2.12891 3.03711 1.8457 3.37891 1.8457C3.73047 1.8457 4.02344 2.12891 4.02344 2.49023L4.02344 18.8477C4.02344 19.1992 3.73047 19.4824 3.37891 19.4824Z", fillAlpha = 0.85f)
        }
        return _sFBookClosedFill!!
    }

private var _sFBookClosedFill: ImageVector? = null
