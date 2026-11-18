package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSlashSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPointerArrowIpadSlashSquareFill: ImageVector
    get() {
        if (_sFPointerArrowIpadSlashSquareFill != null) {
            return _sFPointerArrowIpadSlashSquareFill!!
        }
        _sFPointerArrowIpadSlashSquareFill = sfIcon(
            name = "Monochrome.SFPointerArrowIpadSlashSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM3.64258 4.67773C3.41797 4.89258 3.41797 5.2832 3.64258 5.48828L17.3438 19.1699C17.5781 19.3848 17.9492 19.3848 18.1641 19.1699C18.3887 18.9355 18.3984 18.5742 18.1641 18.3496L4.46289 4.67773C4.23828 4.46289 3.88672 4.45312 3.64258 4.67773ZM7.03125 17.8027C7.03125 18.7207 8.01758 19.0625 8.7793 18.3105L12.002 15.0781L7.03125 10.1074ZM7.03125 5.11719L7.03125 5.99609L16.0156 14.9512L17.0312 14.9512C17.9883 14.9512 18.3301 13.9648 17.7148 13.3301L8.82812 4.46289C8.125 3.75 7.03125 4.10156 7.03125 5.11719Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSlashSquareFill!!
    }

private var _sFPointerArrowIpadSlashSquareFill: ImageVector? = null
