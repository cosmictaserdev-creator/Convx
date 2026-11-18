package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpRightSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowUpRightSquare: ImageVector
    get() {
        if (_sFArrowUpRightSquare != null) {
            return _sFArrowUpRightSquare!!
        }
        _sFArrowUpRightSquare = sfIcon(
            name = "Monochrome.SFArrowUpRightSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M14.1797 7.77344L12.0703 9.74609L6.91406 14.9023C6.76758 15.0586 6.66016 15.2637 6.66016 15.4883C6.66016 15.9766 6.97266 16.2891 7.45117 16.2891C7.71484 16.2891 7.91992 16.1914 8.07617 16.0449L13.2227 10.8984L15.1953 8.79883C15.9863 7.94922 14.9902 7.02148 14.1797 7.77344ZM14.7461 11.1523L14.7461 13.7012C14.7461 14.2188 15.0586 14.541 15.5371 14.541C16.0156 14.541 16.3281 14.1895 16.3281 13.6816L16.3281 7.5293C16.3281 6.89453 15.9863 6.64062 15.4395 6.64062L9.24805 6.64062C8.74023 6.64062 8.41797 6.95312 8.41797 7.42188C8.41797 7.90039 8.75 8.22266 9.26758 8.22266L12.0312 8.22266L15.0195 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpRightSquare!!
    }

private var _sFArrowUpRightSquare: ImageVector? = null
