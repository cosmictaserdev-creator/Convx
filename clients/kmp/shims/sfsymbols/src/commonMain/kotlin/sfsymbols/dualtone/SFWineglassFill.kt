package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWineglassFill (dualtone)
 * Viewport: 18.3789 x 29.4824
 */
public val SfSymbols.Dualtone.SFWineglassFill: ImageVector
    get() {
        if (_sFWineglassFill != null) {
            return _sFWineglassFill!!
        }
        _sFWineglassFill = sfIcon(
            name = "Dualtone.SFWineglassFill",
            viewportWidth = 18.3789f,
            viewportHeight = 29.4824f
        ) {
            addSfPath("M8.99414 29.4629C13.3984 29.4629 16.0742 28.3887 16.0742 26.6016C16.0742 24.9414 13.7207 23.8965 9.82422 23.75L9.82422 18.0859C14.7852 17.7832 18.0176 14.6582 18.0176 10.0391C18.0176 7.49023 16.9531 4.15039 15.3906 1.74805C14.668 0.634766 12.3145 0 9.01367 0C5.69336 0 3.33984 0.634766 2.61719 1.74805C1.05469 4.15039 0 7.49023 0 10.0391C0 14.668 3.23242 17.7832 8.20312 18.0859L8.20312 23.75C4.29688 23.8867 1.95312 24.9414 1.95312 26.6016C1.95312 28.3887 4.59961 29.4629 8.99414 29.4629ZM9.01367 3.04688C6.21094 3.04688 4.12109 2.64648 4.12109 2.26562C4.12109 1.88477 6.2207 1.49414 9.01367 1.49414C11.7969 1.49414 13.8965 1.88477 13.8965 2.26562C13.8965 2.64648 11.8066 3.04688 9.01367 3.04688Z", fillAlpha = 0.85f)
        }
        return _sFWineglassFill!!
    }

private var _sFWineglassFill: ImageVector? = null
