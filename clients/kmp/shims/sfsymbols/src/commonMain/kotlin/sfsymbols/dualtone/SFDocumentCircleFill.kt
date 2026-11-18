package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDocumentCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDocumentCircleFill: ImageVector
    get() {
        if (_sFDocumentCircleFill != null) {
            return _sFDocumentCircleFill!!
        }
        _sFDocumentCircleFill = sfIcon(
            name = "Dualtone.SFDocumentCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.62891 19.6191C8.24219 19.6191 7.54883 18.9258 7.54883 17.5195L7.54883 7.5293C7.54883 6.14258 8.25195 5.42969 9.62891 5.42969L12.1289 5.42969L12.1289 10.5762C12.1289 11.4258 12.5293 11.8262 13.3691 11.8262L18.4668 11.8262L18.4668 17.5195C18.4668 18.9062 17.7734 19.6191 16.3867 19.6191ZM13.4961 10.8594C13.2324 10.8594 13.0957 10.7227 13.0957 10.4492L13.0957 5.50781C13.3398 5.54688 13.6035 5.73242 13.8867 6.02539L17.8711 10.0586C18.1543 10.3516 18.3398 10.6055 18.3789 10.8594Z", fillAlpha = 0.85f)
        }
        return _sFDocumentCircleFill!!
    }

private var _sFDocumentCircleFill: ImageVector? = null
