package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDocumentCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDocumentCircleFill: ImageVector
    get() {
        if (_sFDocumentCircleFill != null) {
            return _sFDocumentCircleFill!!
        }
        _sFDocumentCircleFill = sfIcon(
            name = "Monochrome.SFDocumentCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.62891 5.42969C8.25195 5.42969 7.54883 6.14258 7.54883 7.5293L7.54883 17.5195C7.54883 18.9258 8.24219 19.6191 9.62891 19.6191L16.3867 19.6191C17.7734 19.6191 18.4668 18.9062 18.4668 17.5195L18.4668 11.8262L13.3691 11.8262C12.5293 11.8262 12.1289 11.4258 12.1289 10.5762L12.1289 5.42969ZM13.0957 10.4492C13.0957 10.7227 13.2324 10.8594 13.4961 10.8594L18.3789 10.8594C18.3398 10.6055 18.1543 10.3516 17.8711 10.0586L13.8867 6.02539C13.6035 5.73242 13.3398 5.54688 13.0957 5.50781Z", fillAlpha = 0.85f)
        }
        return _sFDocumentCircleFill!!
    }

private var _sFDocumentCircleFill: ImageVector? = null
