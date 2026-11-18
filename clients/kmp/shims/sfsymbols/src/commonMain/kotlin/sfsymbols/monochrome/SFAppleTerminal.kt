package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppleTerminal (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFAppleTerminal: ImageVector
    get() {
        if (_sFAppleTerminal != null) {
            return _sFAppleTerminal!!
        }
        _sFAppleTerminal = sfIcon(
            name = "Monochrome.SFAppleTerminal",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M5.30273 9.58984C4.43359 10.1074 5.16602 11.416 6.11328 10.8203L9.77539 8.52539C10.4102 8.13477 10.3809 7.19727 9.77539 6.81641L6.11328 4.52148C5.16602 3.92578 4.42383 5.22461 5.30273 5.76172L8.4668 7.67578ZM10.5176 10.9082C10.5176 11.2988 10.8203 11.6113 11.2207 11.6113L16.0156 11.6113C16.416 11.6113 16.7188 11.2988 16.7188 10.9082C16.7188 10.5078 16.416 10.1953 16.0156 10.1953L11.2207 10.1953C10.8203 10.1953 10.5176 10.5078 10.5176 10.9082Z", fillAlpha = 0.85f)
        }
        return _sFAppleTerminal!!
    }

private var _sFAppleTerminal: ImageVector? = null
