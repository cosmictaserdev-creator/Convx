package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLtCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLtCircleFill: ImageVector
    get() {
        if (_sFLtCircleFill != null) {
            return _sFLtCircleFill!!
        }
        _sFLtCircleFill = sfIcon(
            name = "Dualtone.SFLtCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.4375 17.6953C7.94922 17.6953 7.66602 17.3828 7.66602 16.875L7.66602 8.4082C7.66602 7.97852 8.01758 7.62695 8.44727 7.62695C8.86719 7.62695 9.22852 7.97852 9.22852 8.4082L9.22852 16.3672L12.0117 16.3672C12.373 16.3672 12.6758 16.6699 12.6758 17.0215C12.6758 17.3926 12.373 17.6953 12.0117 17.6953ZM15.4883 17.832C15.0488 17.832 14.6973 17.4805 14.6973 17.041L14.6973 9.08203L13.0371 9.08203C12.666 9.08203 12.3828 8.79883 12.3828 8.41797C12.3828 8.05664 12.666 7.76367 13.0371 7.76367L17.832 7.76367C18.1934 7.76367 18.4863 8.05664 18.4863 8.41797C18.4863 8.79883 18.1934 9.08203 17.832 9.08203L16.2695 9.08203L16.2695 17.041C16.2695 17.4805 15.918 17.832 15.4883 17.832Z", fillAlpha = 0.85f)
        }
        return _sFLtCircleFill!!
    }

private var _sFLtCircleFill: ImageVector? = null
