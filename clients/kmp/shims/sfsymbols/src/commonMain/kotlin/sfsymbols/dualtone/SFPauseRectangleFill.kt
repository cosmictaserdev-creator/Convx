package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPauseRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPauseRectangleFill: ImageVector
    get() {
        if (_sFPauseRectangleFill != null) {
            return _sFPauseRectangleFill!!
        }
        _sFPauseRectangleFill = sfIcon(
            name = "Dualtone.SFPauseRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.1914 16.4648C10.5859 16.4648 10.3027 16.1328 10.3027 15.6543L10.3027 7.29492C10.3027 6.81641 10.5859 6.48438 11.1914 6.48438L12.4707 6.48438C13.0859 6.48438 13.3594 6.81641 13.3594 7.29492L13.3594 15.6543C13.3594 16.1328 13.0859 16.4648 12.4707 16.4648ZM17.1387 16.4648C16.5332 16.4648 16.25 16.1328 16.25 15.6543L16.25 7.29492C16.25 6.81641 16.5332 6.48438 17.1387 6.48438L18.418 6.48438C19.0234 6.48438 19.2969 6.81641 19.2969 7.29492L19.2969 15.6543C19.2969 16.1328 19.0234 16.4648 18.418 16.4648Z", fillAlpha = 0.85f)
        }
        return _sFPauseRectangleFill!!
    }

private var _sFPauseRectangleFill: ImageVector? = null
