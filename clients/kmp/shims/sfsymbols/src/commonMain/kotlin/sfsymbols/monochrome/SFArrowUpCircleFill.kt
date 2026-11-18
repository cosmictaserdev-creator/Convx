package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowUpCircleFill: ImageVector
    get() {
        if (_sFArrowUpCircleFill != null) {
            return _sFArrowUpCircleFill!!
        }
        _sFArrowUpCircleFill = sfIcon(
            name = "Monochrome.SFArrowUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.0898 6.55273L7.80273 10.7129C7.62695 10.8789 7.53906 11.0645 7.53906 11.2988C7.53906 11.748 7.87109 12.0801 8.33008 12.0801C8.54492 12.0801 8.79883 11.9922 8.94531 11.8066L11.0742 9.58984L11.8969 8.72335L11.8457 10.625L11.8457 18.3203C11.8457 18.7891 12.2461 19.1895 12.7246 19.1895C13.2129 19.1895 13.6133 18.7891 13.6133 18.3203L13.6133 10.625L13.5549 8.72602L14.375 9.58984L16.5039 11.8066C16.6504 11.9922 16.8848 12.0801 17.1094 12.0801C17.5586 12.0801 17.9102 11.748 17.9102 11.2988C17.9102 11.0645 17.8223 10.8789 17.6465 10.7129L13.3691 6.55273C13.1445 6.32812 12.959 6.25977 12.7246 6.25977C12.5 6.25977 12.3047 6.32812 12.0898 6.55273Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpCircleFill!!
    }

private var _sFArrowUpCircleFill: ImageVector? = null
