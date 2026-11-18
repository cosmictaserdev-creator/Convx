package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraserSlashFill (dualtone)
 * Viewport: 28.5816 x 27.7759
 */
public val SfSymbols.Dualtone.SFEraserSlashFill: ImageVector
    get() {
        if (_sFEraserSlashFill != null) {
            return _sFEraserSlashFill!!
        }
        _sFEraserSlashFill = sfIcon(
            name = "Dualtone.SFEraserSlashFill",
            viewportWidth = 28.5816f,
            viewportHeight = 27.7759f
        ) {
            addSfPath("M14.7107 23.4729L13.3923 24.7913C11.7224 26.4612 9.67164 26.4026 7.90406 24.6545L3.23609 19.9768C1.47828 18.219 1.41968 16.1585 3.0896 14.4885L4.41773 13.1799ZM18.3679 19.8157L15.8142 22.3694L5.52125 12.0764L8.07368 9.52143ZM18.8904 2.36939L25.5115 9.00025C26.5955 10.0745 26.615 11.5784 25.5505 12.6331L21.4699 16.7137L11.1763 6.41565L15.2478 2.34009C16.3123 1.27564 17.8162 1.28541 18.8904 2.36939Z", fillAlpha = 0.425f)
            addSfPath("M25.8044 25.3284C26.1267 25.6506 26.6638 25.6506 26.9861 25.3284C27.3084 24.9963 27.3084 24.4788 26.9861 24.1467L4.44703 1.59791C4.12476 1.27564 3.58765 1.26587 3.25562 1.59791C2.94312 1.92017 2.94312 2.45728 3.25562 2.77955Z", fillAlpha = 0.85f)
        }
        return _sFEraserSlashFill!!
    }

private var _sFEraserSlashFill: ImageVector? = null
