package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLevel (monochrome)
 * Viewport: 30.8203 x 13.252
 */
public val SfSymbols.Monochrome.SFLevel: ImageVector
    get() {
        if (_sFLevel != null) {
            return _sFLevel!!
        }
        _sFLevel = sfIcon(
            name = "Monochrome.SFLevel",
            viewportWidth = 30.8203f,
            viewportHeight = 13.252f
        ) {
            addSfPath("M6.26953 11.5234L5.18555 11.5234L5.18555 1.72852L6.26953 1.72852ZM25.2734 11.5234L24.1797 11.5234L24.1797 1.72852L25.2734 1.72852ZM15.2344 7.08984C11.6733 7.08984 8.99741 4.99176 8.49305 1.72852L9.6837 1.72852C10.1317 4.3654 12.3279 6.05469 15.2344 6.05469C18.1225 6.05469 20.3176 4.3654 20.7655 1.72852L21.9576 1.72852C21.4625 4.99176 18.7955 7.08984 15.2344 7.08984Z", fillAlpha = 0.85f)
            addSfPath("M3.31055 13.252L27.1387 13.252C29.3164 13.252 30.459 12.1094 30.459 9.96094L30.459 3.29102C30.459 1.14258 29.3066 0 27.1289 0L3.30078 0C1.13281 0 0 1.14258 0 3.29102L0 9.96094C0 12.1094 1.14258 13.252 3.31055 13.252ZM3.55469 11.5234C2.39258 11.5234 1.72852 10.8789 1.72852 9.67773L1.72852 3.58398C1.72852 2.38281 2.39258 1.72852 3.56445 1.72852L26.8945 1.72852C28.0469 1.72852 28.7109 2.38281 28.7109 3.58398L28.7109 9.67773C28.7109 10.8789 28.0469 11.5234 26.8848 11.5234ZM15.2344 4.21875C17.002 4.21875 18.3008 3.10547 18.4375 1.42578L12.0117 1.42578C12.1582 3.10547 13.457 4.21875 15.2344 4.21875Z", fillAlpha = 0.85f)
        }
        return _sFLevel!!
    }

private var _sFLevel: ImageVector? = null
