package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNotequalCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFNotequalCircleFill: ImageVector
    get() {
        if (_sFNotequalCircleFill != null) {
            return _sFNotequalCircleFill!!
        }
        _sFNotequalCircleFill = sfIcon(
            name = "Monochrome.SFNotequalCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.6348 7.29492L13.9844 9.52148L7.72461 9.52148C7.13867 9.52148 6.75781 9.81445 6.75781 10.3906C6.75781 10.9473 7.1582 11.2305 7.72461 11.2305L12.7344 11.2305L10.5273 14.248L7.72461 14.248C7.13867 14.248 6.75781 14.541 6.75781 15.1074C6.75781 15.6641 7.1582 15.957 7.72461 15.957L9.26758 15.957L8.41797 17.1289C8.05664 17.5977 8.0957 18.0859 8.53516 18.4082C8.99414 18.7402 9.46289 18.5938 9.78516 18.1348L11.3867 15.957L17.6953 15.957C18.2617 15.957 18.6621 15.6641 18.6621 15.1074C18.6621 14.541 18.2812 14.248 17.6953 14.248L12.6367 14.248L14.8535 11.2305L17.6953 11.2305C18.2617 11.2305 18.6621 10.9473 18.6621 10.3906C18.6621 9.81445 18.2812 9.52148 17.6953 9.52148L16.1035 9.52148L17.002 8.30078C17.3438 7.8418 17.3535 7.35352 16.8945 7.03125C16.4355 6.69922 15.9766 6.80664 15.6348 7.29492Z", fillAlpha = 0.85f)
        }
        return _sFNotequalCircleFill!!
    }

private var _sFNotequalCircleFill: ImageVector? = null
