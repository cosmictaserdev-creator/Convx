package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSlashSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPointerArrowIpadSlashSquareFill: ImageVector
    get() {
        if (_sFPointerArrowIpadSlashSquareFill != null) {
            return _sFPointerArrowIpadSlashSquareFill!!
        }
        _sFPointerArrowIpadSlashSquareFill = sfIcon(
            name = "Dualtone.SFPointerArrowIpadSlashSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M17.7148 13.3301C18.3301 13.9648 17.9883 14.9512 17.0312 14.9512L16.0156 14.9512L7.03125 5.99609L7.03125 5.11719C7.03125 4.10156 8.125 3.75 8.82812 4.46289ZM7.03125 10.1074L12.002 15.0781L8.7793 18.3105C8.01758 19.0625 7.03125 18.7207 7.03125 17.8027ZM3.64258 5.48828C3.41797 5.2832 3.41797 4.89258 3.64258 4.67773C3.88672 4.45312 4.23828 4.46289 4.46289 4.67773L18.1641 18.3496C18.3984 18.5742 18.3887 18.9355 18.1641 19.1699C17.9492 19.3848 17.5781 19.3848 17.3438 19.1699Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSlashSquareFill!!
    }

private var _sFPointerArrowIpadSlashSquareFill: ImageVector? = null
