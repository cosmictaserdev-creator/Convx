package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraserFill (monochrome)
 * Viewport: 24.8192 x 24.6779
 */
public val SfSymbols.Monochrome.SFEraserFill: ImageVector
    get() {
        if (_sFEraserFill != null) {
            return _sFEraserFill!!
        }
        _sFEraserFill = sfIcon(
            name = "Monochrome.SFEraserFill",
            viewportWidth = 24.8192f,
            viewportHeight = 24.6779f
        ) {
            addSfPath("M3.64495 10.5274L13.9379 20.8204L23.6742 11.0841C24.7387 10.0294 24.7192 8.52546 23.6352 7.45124L17.0239 0.820379C15.9399-0.263605 14.436-0.273371 13.3813 0.791082ZM1.35979 18.4278L6.02776 23.1055C7.79534 24.8536 9.84612 24.9122 11.516 23.2423L12.8442 21.9239L2.54143 11.6309L1.21331 12.9395C-0.456613 14.6094-0.39802 16.67 1.35979 18.4278Z", fillAlpha = 0.85f)
        }
        return _sFEraserFill!!
    }

private var _sFEraserFill: ImageVector? = null
