package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectanglePortrait (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFCheckmarkRectanglePortrait: ImageVector
    get() {
        if (_sFCheckmarkRectanglePortrait != null) {
            return _sFCheckmarkRectanglePortrait!!
        }
        _sFCheckmarkRectanglePortrait = sfIcon(
            name = "Dualtone.SFCheckmarkRectanglePortrait",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6445 20.9668 23.125L20.9668 3.80859C20.9668 1.28906 19.707 0.00976562 17.207 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L17.1094 1.73828C18.5156 1.73828 19.2383 2.48047 19.2383 3.84766L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859Z", fillAlpha = 0.425f)
            addSfPath("M9.12109 19.4336C9.48242 19.4336 9.77539 19.2676 9.99023 18.9355L15.9766 9.55078C16.1035 9.35547 16.2305 9.11133 16.2305 8.87695C16.2305 8.39844 15.8008 8.07617 15.3613 8.07617C15.0781 8.07617 14.8047 8.25195 14.6094 8.56445L9.08203 17.3438L6.32812 13.8965C6.07422 13.5645 5.83008 13.4668 5.53711 13.4668C5.06836 13.4668 4.69727 13.8477 4.69727 14.3262C4.69727 14.5605 4.79492 14.7949 4.95117 15L8.21289 18.9355C8.48633 19.2871 8.75977 19.4336 9.12109 19.4336Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectanglePortrait!!
    }

private var _sFCheckmarkRectanglePortrait: ImageVector? = null
