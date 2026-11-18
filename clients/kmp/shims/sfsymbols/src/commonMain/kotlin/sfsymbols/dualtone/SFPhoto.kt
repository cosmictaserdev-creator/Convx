package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhoto (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPhoto: ImageVector
    get() {
        if (_sFPhoto != null) {
            return _sFPhoto!!
        }
        _sFPhoto = sfIcon(
            name = "Dualtone.SFPhoto",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.68164 22.168L25.2441 22.168C27.4609 22.168 28.4961 20.9277 28.5059 18.75L20.9082 11.6699C20.332 11.1328 19.6875 10.8691 18.9746 10.8691C18.2617 10.8691 17.6465 11.123 17.0605 11.6504L11.3379 16.7578L8.98438 14.6094C8.44727 14.1309 7.89062 13.877 7.27539 13.877C6.68945 13.877 6.16211 14.1113 5.6543 14.5898L0.878906 18.9453C1.00586 21.1328 1.8457 22.168 3.68164 22.168ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M9.30664 11.6406C10.9375 11.6406 12.2656 10.3125 12.2656 8.67188C12.2656 7.04102 10.9375 5.69336 9.30664 5.69336C7.66602 5.69336 6.33789 7.04102 6.33789 8.67188C6.33789 10.3125 7.66602 11.6406 9.30664 11.6406Z", fillAlpha = 0.85f)
        }
        return _sFPhoto!!
    }

private var _sFPhoto: ImageVector? = null
