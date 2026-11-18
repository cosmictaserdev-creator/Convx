package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPlayRectangle: ImageVector
    get() {
        if (_sFPlayRectangle != null) {
            return _sFPlayRectangle!!
        }
        _sFPlayRectangle = sfIcon(
            name = "Dualtone.SFPlayRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M12.4316 16.3965L19.6191 12.1289C20.127 11.8262 20.1172 11.1426 19.6191 10.8398L12.4316 6.55273C11.9141 6.25 11.25 6.49414 11.25 7.06055L11.25 15.8984C11.25 16.4648 11.875 16.7285 12.4316 16.3965Z", fillAlpha = 0.85f)
        }
        return _sFPlayRectangle!!
    }

private var _sFPlayRectangle: ImageVector? = null
