package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLtCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLtCircleFill: ImageVector
    get() {
        if (_sFLtCircleFill != null) {
            return _sFLtCircleFill!!
        }
        _sFLtCircleFill = sfIcon(
            name = "Monochrome.SFLtCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM13.0371 7.76367C12.666 7.76367 12.3828 8.05664 12.3828 8.41797C12.3828 8.79883 12.666 9.08203 13.0371 9.08203L14.6973 9.08203L14.6973 17.041C14.6973 17.4805 15.0488 17.832 15.4883 17.832C15.918 17.832 16.2695 17.4805 16.2695 17.041L16.2695 9.08203L17.832 9.08203C18.1934 9.08203 18.4863 8.79883 18.4863 8.41797C18.4863 8.05664 18.1934 7.76367 17.832 7.76367ZM7.66602 8.4082L7.66602 16.875C7.66602 17.3828 7.94922 17.6953 8.4375 17.6953L12.0117 17.6953C12.373 17.6953 12.6758 17.3926 12.6758 17.0215C12.6758 16.6699 12.373 16.3672 12.0117 16.3672L9.22852 16.3672L9.22852 8.4082C9.22852 7.97852 8.86719 7.62695 8.44727 7.62695C8.01758 7.62695 7.66602 7.97852 7.66602 8.4082Z", fillAlpha = 0.85f)
        }
        return _sFLtCircleFill!!
    }

private var _sFLtCircleFill: ImageVector? = null
