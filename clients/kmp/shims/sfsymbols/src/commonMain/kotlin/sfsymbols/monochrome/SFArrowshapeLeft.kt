package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeLeft (monochrome)
 * Viewport: 28.2324 x 22.8223
 */
public val SfSymbols.Monochrome.SFArrowshapeLeft: ImageVector
    get() {
        if (_sFArrowshapeLeft != null) {
            return _sFArrowshapeLeft!!
        }
        _sFArrowshapeLeft = sfIcon(
            name = "Monochrome.SFArrowshapeLeft",
            viewportWidth = 28.2324f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M0 11.4062C0 11.7969 0.146484 12.2461 0.664062 12.7344L10.5859 22.0703C11.1133 22.5684 11.5723 22.8027 12.1387 22.8027C12.9199 22.8027 13.457 22.2363 13.457 21.4648L13.457 16.5137L23.8672 16.5137C25.752 16.5137 26.8262 15.459 26.8262 13.6133L26.8262 9.22852C26.8262 7.38281 25.752 6.33789 23.8672 6.33789L13.457 6.33789L13.457 1.40625C13.457 0.634766 12.9199 0 12.1191 0C11.5527 0 11.1621 0.253906 10.5859 0.791016L0.664062 10.0586C0.136719 10.5566 0 10.9961 0 11.4062ZM1.92383 11.4062C1.92383 11.2988 1.97266 11.1914 2.11914 11.0547L11.2891 2.32422C11.3672 2.26562 11.4453 2.20703 11.543 2.20703C11.6504 2.20703 11.7285 2.28516 11.7285 2.39258L11.7285 7.56836C11.7285 7.8418 11.8457 7.96875 12.1289 7.96875L23.8477 7.96875C24.7168 7.96875 25.1953 8.44727 25.1953 9.30664L25.1953 13.5352C25.1953 14.4043 24.7168 14.8828 23.8477 14.8828L12.1289 14.8828C11.8457 14.8828 11.7285 15 11.7285 15.2832L11.7285 20.3223C11.7285 20.4395 11.6602 20.5176 11.543 20.5176C11.4648 20.5176 11.3867 20.4883 11.2988 20.4004L2.11914 11.7578C1.96289 11.6113 1.92383 11.5137 1.92383 11.4062Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeLeft!!
    }

private var _sFArrowshapeLeft: ImageVector? = null
