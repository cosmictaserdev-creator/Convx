package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownToLineCompact (monochrome)
 * Viewport: 19.2676 x 22.0801
 */
public val SfSymbols.Monochrome.SFArrowDownToLineCompact: ImageVector
    get() {
        if (_sFArrowDownToLineCompact != null) {
            return _sFArrowDownToLineCompact!!
        }
        _sFArrowDownToLineCompact = sfIcon(
            name = "Monochrome.SFArrowDownToLineCompact",
            viewportWidth = 19.2676f,
            viewportHeight = 22.0801f
        ) {
            addSfPath("M18.9062 21.123C18.9062 20.5664 18.5254 20.166 17.9785 20.166L0.947266 20.166C0.390625 20.166 0 20.5664 0 21.123C0 21.6797 0.390625 22.0801 0.947266 22.0801L17.9785 22.0801C18.5254 22.0801 18.9062 21.6797 18.9062 21.123ZM0.917969 9.78516C0.390625 9.78516 0 10.1953 0 10.7227C0 10.9766 0.0878906 11.2012 0.292969 11.416L8.74023 19.8535C8.94531 20.0684 9.18945 20.1855 9.45312 20.1855C9.7168 20.1855 9.96094 20.0684 10.166 19.8535L18.6133 11.416C18.8184 11.2012 18.9062 10.9766 18.9062 10.7227C18.9062 10.1953 18.5156 9.78516 17.9883 9.78516C17.7344 9.78516 17.4707 9.88281 17.3145 10.0488L13.8477 13.4766L9.45312 18.2422L5.05859 13.4766L1.5918 10.0488C1.43555 9.88281 1.17188 9.78516 0.917969 9.78516ZM10.2832 18.3691L10.4004 14.6094L10.4004 0.9375C10.4004 0.390625 10.0098 0 9.45312 0C8.89648 0 8.50586 0.390625 8.50586 0.9375L8.50586 14.6094L8.62305 18.3691C8.64258 18.8184 9.00391 19.1895 9.45312 19.1895C9.90234 19.1895 10.2637 18.8184 10.2832 18.3691Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownToLineCompact!!
    }

private var _sFArrowDownToLineCompact: ImageVector? = null
