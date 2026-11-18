package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectanglePortraitOnRectanglePortraitAngledFill (dualtone)
 * Viewport: 29.1211 x 34.1699
 */
public val SfSymbols.Dualtone.SFRectanglePortraitOnRectanglePortraitAngledFill: ImageVector
    get() {
        if (_sFRectanglePortraitOnRectanglePortraitAngledFill != null) {
            return _sFRectanglePortraitOnRectanglePortraitAngledFill!!
        }
        _sFRectanglePortraitOnRectanglePortraitAngledFill = sfIcon(
            name = "Dualtone.SFRectanglePortraitOnRectanglePortraitAngledFill",
            viewportWidth = 29.1211f,
            viewportHeight = 34.1699f
        ) {
            addSfPath("M20.4883 4.32617L20.7928 6.05469L11.8457 6.05469C8.48633 6.05469 6.52344 8.02734 6.52344 11.416L6.52344 27.8501C5.35226 27.4762 4.61525 26.4657 4.32617 24.834L1.30859 7.70508C0.869141 5.21484 1.89453 3.74023 4.3457 3.31055L16.123 1.23047C18.5742 0.800781 20.0488 1.83594 20.4883 4.32617Z", fillAlpha = 0.425f)
            addSfPath("M27.5586 11.416C27.5586 8.88672 26.2988 7.61719 23.7988 7.61719L11.8457 7.61719C9.3457 7.61719 8.08594 8.88672 8.08594 11.416L8.08594 28.8086C8.08594 31.3281 9.36523 32.6172 11.8457 32.6172L23.7988 32.6172C26.2793 32.6172 27.5586 31.3281 27.5586 28.8086Z", fillAlpha = 0.85f)
        }
        return _sFRectanglePortraitOnRectanglePortraitAngledFill!!
    }

private var _sFRectanglePortraitOnRectanglePortraitAngledFill: ImageVector? = null
