package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownToLineCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowDownToLineCircleFill: ImageVector
    get() {
        if (_sFArrowDownToLineCircleFill != null) {
            return _sFArrowDownToLineCircleFill!!
        }
        _sFArrowDownToLineCircleFill = sfIcon(
            name = "Monochrome.SFArrowDownToLineCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.88086 18.0469C7.44141 18.0469 7.13867 18.3691 7.13867 18.7891C7.13867 19.2383 7.44141 19.5605 7.88086 19.5605L17.5586 19.5605C17.9883 19.5605 18.291 19.2383 18.291 18.7891C18.291 18.3691 17.9883 18.0469 17.5586 18.0469ZM11.9434 6.14258L11.9434 12.373L12.0703 14.7559L9.69727 12.1582L8.76953 11.1621C8.62305 10.9961 8.4375 10.9277 8.20312 10.9277C7.76367 10.9277 7.4707 11.2305 7.4707 11.6602C7.4707 11.8555 7.56836 12.0898 7.71484 12.2266L12.1387 16.7285C12.3047 16.9043 12.5 16.9922 12.7051 16.9922C12.9199 16.9922 13.1055 16.8848 13.2715 16.7285L17.6953 12.2559C17.8809 12.0801 17.959 11.8848 17.959 11.6504C17.959 11.25 17.666 10.9277 17.2363 10.9277C17.0215 10.9277 16.8164 10.9961 16.6699 11.1621L15.6641 12.1777L13.3398 14.7461L13.457 12.373L13.457 6.14258C13.457 5.70312 13.1348 5.39062 12.7051 5.39062C12.2461 5.39062 11.9434 5.70312 11.9434 6.14258Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownToLineCircleFill!!
    }

private var _sFArrowDownToLineCircleFill: ImageVector? = null
