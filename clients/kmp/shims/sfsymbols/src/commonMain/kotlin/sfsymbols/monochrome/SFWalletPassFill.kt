package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWalletPassFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFWalletPassFill: ImageVector
    get() {
        if (_sFWalletPassFill != null) {
            return _sFWalletPassFill!!
        }
        _sFWalletPassFill = sfIcon(
            name = "Monochrome.SFWalletPassFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M7.68555 0.654297C7.68555 0.693359 7.68555 0.751953 7.68555 0.800781C7.68555 2.41211 8.81836 3.4375 10.4883 3.4375C12.1484 3.4375 13.2812 2.41211 13.2812 0.800781C13.2812 0.751953 13.2812 0.693359 13.2812 0.654297C13.2812 0.263672 13.5254 0.00976562 13.8867 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.2793 20.9668 3.80859L20.9668 23.125C20.9668 25.6348 19.6875 26.9238 17.207 26.9238L3.75977 26.9238C1.2793 26.9238 0 25.6348 0 23.125L0 3.80859C0 1.2793 1.25977 0.00976562 3.75977 0.00976562L7.08008 0.00976562C7.44141 0.00976562 7.68555 0.263672 7.68555 0.654297ZM5.60547 12.3242C5.20508 12.3242 4.91211 12.627 4.91211 13.0078C4.91211 13.3887 5.20508 13.6816 5.60547 13.6816L10.2832 13.6816C10.6738 13.6816 10.9668 13.3887 10.9668 13.0078C10.9668 12.627 10.6738 12.3242 10.2832 12.3242ZM5.60547 7.76367C5.20508 7.76367 4.91211 8.05664 4.91211 8.4375C4.91211 8.82812 5.20508 9.13086 5.60547 9.13086L15.3809 9.13086C15.7617 9.13086 16.0547 8.82812 16.0547 8.4375C16.0547 8.05664 15.7617 7.76367 15.3809 7.76367Z", fillAlpha = 0.85f)
        }
        return _sFWalletPassFill!!
    }

private var _sFWalletPassFill: ImageVector? = null
