package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeTurnUpBackwardFill (dualtone)
 * Viewport: 28.2324 x 22.8223
 */
public val SfSymbols.Dualtone.SFArrowshapeTurnUpBackwardFill: ImageVector
    get() {
        if (_sFArrowshapeTurnUpBackwardFill != null) {
            return _sFArrowshapeTurnUpBackwardFill!!
        }
        _sFArrowshapeTurnUpBackwardFill = sfIcon(
            name = "Dualtone.SFArrowshapeTurnUpBackwardFill",
            viewportWidth = 28.2324f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M9.6875 6.33789C6.86523 6.33789 4.59961 8.60352 4.59961 11.416C4.59961 14.2383 6.86523 16.5137 9.6875 16.5137L13.8672 16.5137C18.9941 16.5137 22.1777 17.832 24.2578 21.9238C24.6484 22.666 25.1465 22.8027 25.6543 22.8027C26.2695 22.8027 26.8262 22.2656 26.8262 21.2988C26.8262 11.7871 22.6172 6.33789 13.8672 6.33789ZM13.457 21.4648L13.457 1.40625C13.457 0.634766 12.9199 0 12.1191 0C11.5527 0 11.1621 0.253906 10.5859 0.791016L0.664062 10.0586C0.136719 10.5566 0 10.9961 0 11.4062C0 11.7969 0.146484 12.2461 0.664062 12.7344L10.5859 22.0703C11.1133 22.5684 11.5723 22.8027 12.1387 22.8027C12.9199 22.8027 13.457 22.2363 13.457 21.4648Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeTurnUpBackwardFill!!
    }

private var _sFArrowshapeTurnUpBackwardFill: ImageVector? = null
