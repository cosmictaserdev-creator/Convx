package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGuidepointHorizontal (monochrome)
 * Viewport: 34.9023 x 8.54492
 */
public val SfSymbols.Monochrome.SFGuidepointHorizontal: ImageVector
    get() {
        if (_sFGuidepointHorizontal != null) {
            return _sFGuidepointHorizontal!!
        }
        _sFGuidepointHorizontal = sfIcon(
            name = "Monochrome.SFGuidepointHorizontal",
            viewportWidth = 34.9023f,
            viewportHeight = 8.54492f
        ) {
            addSfPath("M30.3027 8.54492C32.6367 8.54492 34.541 6.65039 34.541 4.29688C34.541 1.95312 32.6367 0.0585938 30.3027 0.0585938C27.9492 0.0585938 26.0449 1.95312 26.0449 4.29688C26.0449 6.65039 27.9492 8.54492 30.3027 8.54492ZM4.23828 8.54492C6.5918 8.54492 8.48633 6.65039 8.48633 4.30664C8.48633 1.95312 6.5918 0.0585938 4.23828 0.0585938C1.89453 0.0585938 0 1.95312 0 4.30664C0 6.65039 1.89453 8.54492 4.23828 8.54492ZM5.75195 5.17578L27.998 5.17578C28.4766 5.17578 28.8672 4.78516 28.8672 4.30664C28.8672 3.83789 28.4766 3.44727 27.998 3.44727L5.75195 3.44727C5.27344 3.44727 4.88281 3.83789 4.88281 4.30664C4.88281 4.78516 5.27344 5.17578 5.75195 5.17578Z", fillAlpha = 0.85f)
        }
        return _sFGuidepointHorizontal!!
    }

private var _sFGuidepointHorizontal: ImageVector? = null
