package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeLeftFill (monochrome)
 * Viewport: 28.2324 x 22.8223
 */
public val SfSymbols.Monochrome.SFArrowshapeLeftFill: ImageVector
    get() {
        if (_sFArrowshapeLeftFill != null) {
            return _sFArrowshapeLeftFill!!
        }
        _sFArrowshapeLeftFill = sfIcon(
            name = "Monochrome.SFArrowshapeLeftFill",
            viewportWidth = 28.2324f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M24.0625 6.5918L8.54492 6.5918C6.78711 6.5918 5.78125 7.56836 5.78125 9.28711L5.78125 13.5547C5.78125 15.2832 6.78711 16.25 8.54492 16.25L24.0625 16.25C25.8203 16.25 26.8262 15.2832 26.8262 13.5547L26.8262 9.28711C26.8262 7.56836 25.8203 6.5918 24.0625 6.5918ZM13.457 21.4648L13.457 1.40625C13.457 0.634766 12.9199 0 12.1191 0C11.5527 0 11.1621 0.253906 10.5859 0.791016L0.664062 10.0586C0.136719 10.5566 0 10.9961 0 11.4062C0 11.7969 0.146484 12.2461 0.664062 12.7344L10.5859 22.0703C11.1133 22.5684 11.5723 22.8027 12.1387 22.8027C12.9199 22.8027 13.457 22.2363 13.457 21.4648Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeLeftFill!!
    }

private var _sFArrowshapeLeftFill: ImageVector? = null
