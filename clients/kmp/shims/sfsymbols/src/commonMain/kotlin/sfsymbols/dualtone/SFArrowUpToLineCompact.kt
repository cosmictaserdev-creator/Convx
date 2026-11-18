package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpToLineCompact (dualtone)
 * Viewport: 19.2676 x 22.0801
 */
public val SfSymbols.Dualtone.SFArrowUpToLineCompact: ImageVector
    get() {
        if (_sFArrowUpToLineCompact != null) {
            return _sFArrowUpToLineCompact!!
        }
        _sFArrowUpToLineCompact = sfIcon(
            name = "Dualtone.SFArrowUpToLineCompact",
            viewportWidth = 19.2676f,
            viewportHeight = 22.0801f
        ) {
            addSfPath("M18.9062 0.957031C18.9062 0.400391 18.5254 0 17.9785 0L0.947266 0C0.390625 0 0 0.400391 0 0.957031C0 1.51367 0.390625 1.91406 0.947266 1.91406L17.9785 1.91406C18.5254 1.91406 18.9062 1.51367 18.9062 0.957031ZM0.917969 12.2949C1.17188 12.2949 1.43555 12.1973 1.5918 12.0312L5.05859 8.60352L9.45312 3.83789L13.8477 8.60352L17.3145 12.0312C17.4707 12.1973 17.7344 12.2949 17.9883 12.2949C18.5156 12.2949 18.9062 11.8848 18.9062 11.3574C18.9062 11.1035 18.8184 10.8789 18.6133 10.6641L10.166 2.22656C9.96094 2.01172 9.7168 1.89453 9.45312 1.89453C9.18945 1.89453 8.94531 2.01172 8.74023 2.22656L0.292969 10.6641C0.0878906 10.8789 0 11.1035 0 11.3574C0 11.8848 0.390625 12.2949 0.917969 12.2949ZM10.2832 3.71094C10.2637 3.26172 9.90234 2.89062 9.45312 2.89062C9.00391 2.89062 8.64258 3.26172 8.62305 3.71094L8.50586 7.4707L8.50586 21.1426C8.50586 21.6895 8.89648 22.0801 9.45312 22.0801C10.0098 22.0801 10.4004 21.6895 10.4004 21.1426L10.4004 7.4707Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpToLineCompact!!
    }

private var _sFArrowUpToLineCompact: ImageVector? = null
