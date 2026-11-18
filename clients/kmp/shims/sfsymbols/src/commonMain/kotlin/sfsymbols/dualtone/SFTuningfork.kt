package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTuningfork (dualtone)
 * Viewport: 26.6211 x 26.3574
 */
public val SfSymbols.Dualtone.SFTuningfork: ImageVector
    get() {
        if (_sFTuningfork != null) {
            return _sFTuningfork!!
        }
        _sFTuningfork = sfIcon(
            name = "Dualtone.SFTuningfork",
            viewportWidth = 26.6211f,
            viewportHeight = 26.3574f
        ) {
            addSfPath("M6.875 13.1738L6.86523 17.9492L1.79688 23.0273L3.32031 24.541L8.32031 19.5605L13.2422 19.5312C14.082 19.5312 14.9316 19.2578 15.5762 18.623L25.9473 8.35938C26.377 7.93945 26.3574 7.26562 25.9473 6.83594C25.5371 6.41602 24.834 6.41602 24.4238 6.8457L14.375 16.9238C13.9844 17.3145 13.4863 17.5586 12.9004 17.5586L9.14062 17.5586L8.98438 17.3633L8.98438 13.3984C8.98438 12.8125 9.21875 12.3145 9.60938 11.9141L19.5703 1.98242C19.9902 1.57227 19.9707 0.878906 19.5703 0.458984C19.1602 0.0390625 18.457 0.0292969 18.0469 0.458984L7.79297 10.8398C7.13867 11.5039 6.88477 12.334 6.875 13.1738ZM2.16797 26.3574C3.35938 26.3574 4.33594 25.3809 4.33594 24.1895C4.33594 22.9785 3.35938 22.002 2.1582 22.0117C0.966797 22.0215 0 22.9785 0 24.1895C0 25.3809 0.966797 26.3574 2.16797 26.3574Z", fillAlpha = 0.85f)
        }
        return _sFTuningfork!!
    }

private var _sFTuningfork: ImageVector? = null
