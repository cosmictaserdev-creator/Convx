package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMedalFill (dualtone)
 * Viewport: 23.4383 x 27.7832
 */
public val SfSymbols.Dualtone.SFMedalFill: ImageVector
    get() {
        if (_sFMedalFill != null) {
            return _sFMedalFill!!
        }
        _sFMedalFill = sfIcon(
            name = "Dualtone.SFMedalFill",
            viewportWidth = 23.4383f,
            viewportHeight = 27.7832f
        ) {
            addSfPath("M11.5336 27.7832C15.85 27.7832 19.3461 24.2871 19.3461 19.9707C19.3461 15.6445 15.85 12.1484 11.5336 12.1484C7.20744 12.1484 3.72111 15.6445 3.72111 19.9707C3.72111 24.2871 7.20744 27.7832 11.5336 27.7832ZM9.23869 24.0527C8.82853 24.3555 8.38908 24.043 8.55509 23.5547L9.45353 20.8398L7.13908 19.1895C6.74845 18.9062 6.87541 18.3496 7.40275 18.3496L10.2445 18.3691L11.1235 15.6543C11.2797 15.1758 11.807 15.1758 11.9535 15.6543L12.8324 18.3691L15.684 18.3496C16.2113 18.3496 16.309 18.916 15.9477 19.1797L13.6235 20.8398L14.5317 23.5547C14.6977 24.043 14.2582 24.3555 13.8383 24.0527L11.5434 22.373ZM4.16056 14.1797C5.36174 12.6562 7.03166 11.5039 8.95549 10.9668L3.44767 0.673828L0.146891 5.72266C-0.0386556 6.01562-0.0484212 6.33789 0.117594 6.62109ZM8.64299 6.48438L14.434 6.48438L17.9594 0.0195312L5.10783 0.0195312ZM14.1117 10.9668C16.0453 11.5137 17.7055 12.6562 18.9164 14.1895L22.9594 6.62109C23.1254 6.33789 23.1156 6.01562 22.9301 5.72266L19.6195 0.673828Z", fillAlpha = 0.85f)
        }
        return _sFMedalFill!!
    }

private var _sFMedalFill: ImageVector? = null
