package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKipsign (dualtone)
 * Viewport: 17.7637 x 23.0176
 */
public val SfSymbols.Dualtone.SFKipsign: ImageVector
    get() {
        if (_sFKipsign != null) {
            return _sFKipsign!!
        }
        _sFKipsign = sfIcon(
            name = "Dualtone.SFKipsign",
            viewportWidth = 17.7637f,
            viewportHeight = 23.0176f
        ) {
            addSfPath("M15.8594 22.998C16.3867 22.998 16.8359 22.6074 16.8359 22.0605C16.8359 21.7969 16.748 21.582 16.5527 21.3379L8.30078 11.4258L16.5332 1.65039C16.6895 1.45508 16.8262 1.17188 16.8262 0.927734C16.8262 0.410156 16.4062 0 15.8496 0C15.4883 0 15.1855 0.224609 14.9609 0.507812L5.92773 11.4258L14.9707 22.4805C15.2246 22.8027 15.4785 22.998 15.8594 22.998ZM3.66211 22.998C4.26758 22.998 4.7168 22.5391 4.7168 21.9141L4.7168 1.08398C4.7168 0.458984 4.26758 0 3.66211 0C3.04688 0 2.58789 0.458984 2.58789 1.08398L2.58789 21.9141C2.58789 22.5391 3.04688 22.998 3.66211 22.998ZM0 11.416C0 11.8262 0.302734 12.1387 0.722656 12.1387L16.6797 12.1387C17.0996 12.1387 17.4023 11.8262 17.4023 11.416C17.4023 11.0059 17.0996 10.7031 16.6797 10.7031L0.722656 10.7031C0.302734 10.7031 0 11.0059 0 11.416Z", fillAlpha = 0.85f)
        }
        return _sFKipsign!!
    }

private var _sFKipsign: ImageVector? = null
