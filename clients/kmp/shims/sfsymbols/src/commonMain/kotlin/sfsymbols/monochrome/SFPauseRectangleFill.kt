package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPauseRectangleFill: ImageVector
    get() {
        if (_sFPauseRectangleFill != null) {
            return _sFPauseRectangleFill!!
        }
        _sFPauseRectangleFill = sfIcon(
            name = "Monochrome.SFPauseRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM11.1914 6.48438C10.5859 6.48438 10.3027 6.81641 10.3027 7.29492L10.3027 15.6543C10.3027 16.1328 10.5859 16.4648 11.1914 16.4648L12.4707 16.4648C13.0859 16.4648 13.3594 16.1328 13.3594 15.6543L13.3594 7.29492C13.3594 6.81641 13.0859 6.48438 12.4707 6.48438ZM17.1387 6.48438C16.5332 6.48438 16.25 6.81641 16.25 7.29492L16.25 15.6543C16.25 16.1328 16.5332 16.4648 17.1387 16.4648L18.418 16.4648C19.0234 16.4648 19.2969 16.1328 19.2969 15.6543L19.2969 7.29492C19.2969 6.81641 19.0234 6.48438 18.418 6.48438Z", fillAlpha = 0.85f)
        }
        return _sFPauseRectangleFill!!
    }

private var _sFPauseRectangleFill: ImageVector? = null
