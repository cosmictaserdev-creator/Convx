package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwatchpaletteFill (dualtone)
 * Viewport: 30.6055 x 31.9238
 */
public val SfSymbols.Dualtone.SFSwatchpaletteFill: ImageVector
    get() {
        if (_sFSwatchpaletteFill != null) {
            return _sFSwatchpaletteFill!!
        }
        _sFSwatchpaletteFill = sfIcon(
            name = "Dualtone.SFSwatchpaletteFill",
            viewportWidth = 30.6055f,
            viewportHeight = 31.9238f
        ) {
            addSfPath("M20.8984 23.75L14.4847 27.4496L18.754 20.0385ZM25.9961 12.1777L28.1738 15.9668C29.1309 17.6074 28.7109 19.248 27.1094 20.166L22.2461 22.9785L19.6508 18.4818L24.0137 10.9082C24.0736 10.8038 24.1296 10.6993 24.1762 10.595C24.8862 10.8139 25.5192 11.3479 25.9961 12.1777Z", fillAlpha = 0.2125f)
            addSfPath("M14.1211 24.9316L13.7695 25.5495L13.7695 24.7289ZM19.082 16.3477L14.9023 23.584L13.7695 22.9302L13.7695 13.2782ZM17.7148 3.76953L21.5039 5.95703C23.1445 6.9043 23.5938 8.52539 22.6758 10.127L19.8633 14.9902L13.7695 11.4758L13.7695 4.59961C13.7695 4.50752 13.7674 4.41642 13.7578 4.32896C14.7157 3.12209 16.1958 2.88777 17.7148 3.76953Z", fillAlpha = 0.425f)
            addSfPath("M1.55273 10.2148L12.2168 10.2148L12.2168 4.59961C12.2168 2.73438 11.0254 1.5625 9.12109 1.5625L4.74609 1.5625C2.7832 1.5625 1.55273 2.73438 1.55273 4.59961ZM1.55273 20.1367L12.2168 20.1367L12.2168 11.7676L1.55273 11.7676ZM4.74609 30.3418L9.12109 30.3418C11.0254 30.3418 12.2168 29.1699 12.2168 27.3047L12.2168 21.6895L1.55273 21.6895L1.55273 27.3047C1.55273 29.1699 2.7832 30.3418 4.74609 30.3418Z", fillAlpha = 0.85f)
        }
        return _sFSwatchpaletteFill!!
    }

private var _sFSwatchpaletteFill: ImageVector? = null
