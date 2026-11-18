package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownRightSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowDownRightSquare: ImageVector
    get() {
        if (_sFArrowDownRightSquare != null) {
            return _sFArrowDownRightSquare!!
        }
        _sFArrowDownRightSquare = sfIcon(
            name = "Dualtone.SFArrowDownRightSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M14.1797 15.1855C14.9805 15.9375 15.9863 15 15.1855 14.1602L13.2129 12.0605L8.06641 6.91406C7.91016 6.75781 7.71484 6.66016 7.44141 6.66016C6.96289 6.66016 6.65039 6.97266 6.65039 7.46094C6.65039 7.68555 6.75781 7.89062 6.91406 8.04688L12.0703 13.2031ZM14.7363 11.8066L15.0098 15.0293L12.0312 14.7363L9.25781 14.7363C8.75 14.7363 8.4082 15.0488 8.4082 15.5273C8.4082 16.0059 8.74023 16.3086 9.23828 16.3086L15.4395 16.3086C15.9766 16.3086 16.3281 16.0547 16.3281 15.4297L16.3281 9.26758C16.3281 8.75977 16.0059 8.4082 15.5371 8.4082C15.0586 8.4082 14.7363 8.73047 14.7363 9.24805Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownRightSquare!!
    }

private var _sFArrowDownRightSquare: ImageVector? = null
