package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorGarageOpen (dualtone)
 * Viewport: 30.5176 x 26.4551
 */
public val SfSymbols.Dualtone.SFDoorGarageOpen: ImageVector
    get() {
        if (_sFDoorGarageOpen != null) {
            return _sFDoorGarageOpen!!
        }
        _sFDoorGarageOpen = sfIcon(
            name = "Dualtone.SFDoorGarageOpen",
            viewportWidth = 30.5176f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L27.7832 1.73828C28.1543 1.73828 28.418 2.00195 28.418 2.40234L28.418 25.5664C28.418 26.0449 28.7988 26.4355 29.2871 26.4355C29.7656 26.4355 30.1562 26.0449 30.1562 25.5664L30.1562 2.28516C30.1562 0.917969 29.2188 0 27.8125 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355Z", fillAlpha = 0.425f)
            addSfPath("M3.4668 8.36914L26.6895 8.36914C26.8945 8.36914 27.0508 8.20312 27.0508 7.99805L27.0508 3.4668C27.0508 3.26172 26.8945 3.0957 26.6895 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 7.99805C3.0957 8.20312 3.26172 8.36914 3.4668 8.36914ZM12.5586 6.30859C12.2168 6.30859 11.9824 6.07422 11.9824 5.73242C11.9824 5.39062 12.2168 5.14648 12.5586 5.14648L17.5879 5.14648C17.9297 5.14648 18.1738 5.39062 18.1738 5.73242C18.1738 6.07422 17.9297 6.30859 17.5879 6.30859Z", fillAlpha = 0.85f)
        }
        return _sFDoorGarageOpen!!
    }

private var _sFDoorGarageOpen: ImageVector? = null
