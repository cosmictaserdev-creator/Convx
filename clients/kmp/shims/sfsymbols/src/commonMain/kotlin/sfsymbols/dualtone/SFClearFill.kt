package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClearFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFClearFill: ImageVector
    get() {
        if (_sFClearFill != null) {
            return _sFClearFill!!
        }
        _sFClearFill = sfIcon(
            name = "Dualtone.SFClearFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.24609 16.6309C6.74805 16.6309 6.34766 16.2305 6.34766 15.7227C6.34766 15.4883 6.44531 15.2637 6.61133 15.0879L10.2148 11.4844L6.61133 7.88086C6.44531 7.70508 6.34766 7.49023 6.34766 7.24609C6.34766 6.74805 6.74805 6.36719 7.24609 6.36719C7.5 6.36719 7.69531 6.45508 7.87109 6.63086L11.4844 10.2344L15.1172 6.62109C15.3027 6.42578 15.5078 6.34766 15.752 6.34766C16.2402 6.34766 16.6406 6.73828 16.6406 7.22656C16.6406 7.48047 16.5723 7.67578 16.377 7.87109L12.7637 11.4844L16.3672 15.0781C16.5527 15.2539 16.6309 15.4785 16.6309 15.7227C16.6309 16.2305 16.2305 16.6309 15.7324 16.6309C15.4785 16.6309 15.2441 16.5332 15.0781 16.3574L11.4844 12.7539L7.90039 16.3574C7.72461 16.5332 7.5 16.6309 7.24609 16.6309Z", fillAlpha = 0.85f)
        }
        return _sFClearFill!!
    }

private var _sFClearFill: ImageVector? = null
