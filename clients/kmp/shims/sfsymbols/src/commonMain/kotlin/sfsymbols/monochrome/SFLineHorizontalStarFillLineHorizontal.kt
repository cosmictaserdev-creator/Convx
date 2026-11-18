package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLineHorizontalStarFillLineHorizontal (monochrome)
 * Viewport: 31.9238 x 23.6816
 */
public val SfSymbols.Monochrome.SFLineHorizontalStarFillLineHorizontal: ImageVector
    get() {
        if (_sFLineHorizontalStarFillLineHorizontal != null) {
            return _sFLineHorizontalStarFillLineHorizontal!!
        }
        _sFLineHorizontalStarFillLineHorizontal = sfIcon(
            name = "Monochrome.SFLineHorizontalStarFillLineHorizontal",
            viewportWidth = 31.9238f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M0 22.8027C0 23.2715 0.390625 23.6719 0.869141 23.6719L30.7031 23.6719C31.1719 23.6719 31.5625 23.2715 31.5625 22.8027C31.5625 22.3242 31.1719 21.9336 30.7031 21.9336L0.869141 21.9336C0.390625 21.9336 0 22.3242 0 22.8027ZM0 0.859375C0 1.33789 0.390625 1.72852 0.869141 1.72852L30.7031 1.72852C31.1719 1.72852 31.5625 1.33789 31.5625 0.859375C31.5625 0.390625 31.1719 0 30.7031 0L0.869141 0C0.390625 0 0 0.390625 0 0.859375Z", fillAlpha = 0.85f)
            addSfPath("M12.4512 17.5879L15.791 15.1367L19.1309 17.5879C19.6973 17.998 20.3516 17.6172 20.1074 16.875L18.7988 12.9492L22.207 10.498C22.8027 10.0781 22.5488 9.36523 21.8555 9.36523L17.6465 9.36523L16.3867 5.45898C16.1816 4.80469 15.4004 4.80469 15.1953 5.45898L13.9258 9.36523L9.70703 9.36523C9.02344 9.36523 8.76953 10.0781 9.36523 10.498L12.7832 12.9492L11.4746 16.875C11.2305 17.6172 11.875 17.998 12.4512 17.5879Z", fillAlpha = 0.85f)
        }
        return _sFLineHorizontalStarFillLineHorizontal!!
    }

private var _sFLineHorizontalStarFillLineHorizontal: ImageVector? = null
