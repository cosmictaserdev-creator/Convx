package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPlusRectangle: ImageVector
    get() {
        if (_sFPlusRectangle != null) {
            return _sFPlusRectangle!!
        }
        _sFPlusRectangle = sfIcon(
            name = "Monochrome.SFPlusRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M15.7031 16.3086L15.7031 6.61133C15.7031 6.06445 15.3223 5.68359 14.7852 5.68359C14.2676 5.68359 13.8965 6.06445 13.8965 6.61133L13.8965 16.3086C13.8965 16.8457 14.2676 17.2266 14.7852 17.2266C15.3223 17.2266 15.7031 16.8555 15.7031 16.3086ZM9.95117 12.3535L19.6582 12.3535C20.1855 12.3535 20.5664 11.9922 20.5664 11.4746C20.5664 10.9277 20.1953 10.5566 19.6582 10.5566L9.95117 10.5566C9.4043 10.5566 9.0332 10.9277 9.0332 11.4746C9.0332 11.9922 9.41406 12.3535 9.95117 12.3535Z", fillAlpha = 0.85f)
        }
        return _sFPlusRectangle!!
    }

private var _sFPlusRectangle: ImageVector? = null
