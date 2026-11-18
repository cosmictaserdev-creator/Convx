package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrophyFill (monochrome)
 * Viewport: 24.2383 x 27.1191
 */
public val SfSymbols.Monochrome.SFTrophyFill: ImageVector
    get() {
        if (_sFTrophyFill != null) {
            return _sFTrophyFill!!
        }
        _sFTrophyFill = sfIcon(
            name = "Monochrome.SFTrophyFill",
            viewportWidth = 24.2383f,
            viewportHeight = 27.1191f
        ) {
            addSfPath("M0 3.97461C0 9.00391 2.32422 12.0508 6.99219 13.6621C7.83203 14.7754 8.82812 15.6641 9.79492 16.25L9.79492 21.6406L7.67578 21.6406C6.04492 21.6406 5.17578 22.5781 5.17578 24.1309L5.17578 26.2891C5.17578 26.7773 5.55664 27.0996 6.00586 27.0996L17.8613 27.0996C18.3203 27.0996 18.7012 26.7773 18.7012 26.2891L18.7012 24.1309C18.7012 22.5781 17.832 21.6406 16.2012 21.6406L14.082 21.6406L14.082 16.25C15.0488 15.6641 16.0449 14.7754 16.8848 13.6621C21.5527 12.0508 23.877 9.00391 23.877 3.97461C23.877 2.76367 23.1055 2.02148 21.8359 2.02148L19.541 2.02148C19.3848 0.761719 18.5254 0 17.0898 0L6.78711 0C5.37109 0 4.49219 0.742188 4.33594 2.02148L2.04102 2.02148C0.771484 2.02148 0 2.76367 0 3.97461ZM1.55273 4.12109C1.55273 3.84766 1.74805 3.65234 2.02148 3.65234L4.28711 3.65234L4.28711 6.29883C4.28711 8.21289 4.78516 9.98047 5.55664 11.5039C2.93945 10.1172 1.55273 7.71484 1.55273 4.12109ZM18.3203 11.5039C19.0918 9.98047 19.5898 8.21289 19.5898 6.29883L19.5898 3.65234L21.8457 3.65234C22.1289 3.65234 22.3242 3.84766 22.3242 4.12109C22.3242 7.71484 20.9375 10.1172 18.3203 11.5039Z", fillAlpha = 0.85f)
        }
        return _sFTrophyFill!!
    }

private var _sFTrophyFill: ImageVector? = null
