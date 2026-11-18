package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDirectcurrent (monochrome)
 * Viewport: 24.9512 x 7.90039
 */
public val SfSymbols.Monochrome.SFDirectcurrent: ImageVector
    get() {
        if (_sFDirectcurrent != null) {
            return _sFDirectcurrent!!
        }
        _sFDirectcurrent = sfIcon(
            name = "Monochrome.SFDirectcurrent",
            viewportWidth = 24.9512f,
            viewportHeight = 7.90039f
        ) {
            addSfPath("M0 0.917969C0 1.41602 0.410156 1.82617 0.917969 1.82617L23.6719 1.82617C24.1699 1.82617 24.5898 1.41602 24.5898 0.917969C24.5898 0.410156 24.1699 0 23.6719 0L0.917969 0C0.410156 0 0 0.410156 0 0.917969ZM0 6.97266C0 7.4707 0.410156 7.88086 0.917969 7.88086L5.35156 7.88086C5.85938 7.88086 6.26953 7.4707 6.26953 6.97266C6.26953 6.46484 5.85938 6.05469 5.35156 6.05469L0.917969 6.05469C0.410156 6.05469 0 6.46484 0 6.97266ZM9.16992 6.97266C9.16992 7.4707 9.58008 7.88086 10.0781 7.88086L14.502 7.88086C15.0098 7.88086 15.4199 7.4707 15.4199 6.97266C15.4199 6.46484 15.0098 6.05469 14.502 6.05469L10.0781 6.05469C9.58008 6.05469 9.16992 6.46484 9.16992 6.97266ZM18.3203 6.97266C18.3203 7.4707 18.7305 7.88086 19.2285 7.88086L23.5938 7.88086C24.0918 7.88086 24.502 7.4707 24.502 6.97266C24.502 6.46484 24.0918 6.05469 23.5938 6.05469L19.2285 6.05469C18.7305 6.05469 18.3203 6.46484 18.3203 6.97266Z", fillAlpha = 0.85f)
        }
        return _sFDirectcurrent!!
    }

private var _sFDirectcurrent: ImageVector? = null
