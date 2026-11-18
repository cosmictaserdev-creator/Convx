package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonSlash (dualtone)
 * Viewport: 28.5353 x 28.4499
 */
public val SfSymbols.Dualtone.SFPersonSlash: ImageVector
    get() {
        if (_sFPersonSlash != null) {
            return _sFPersonSlash!!
        }
        _sFPersonSlash = sfIcon(
            name = "Dualtone.SFPersonSlash",
            viewportWidth = 28.5353f,
            viewportHeight = 28.4499f
        ) {
            addSfPath("M14.6066 17.5131C14.4408 17.4952 14.2651 17.4916 14.087 17.4916C8.21785 17.4916 4.9073 21.3978 4.9073 23.6732C4.9073 24.0248 5.07332 24.1517 5.5616 24.1517L21.2452 24.1517L22.844 25.7506C22.6254 25.7722 22.3884 25.7826 22.1339 25.7826L6.03035 25.7826C4.09675 25.7826 3.16902 25.1771 3.16902 23.8685C3.16902 20.7594 6.87321 16.3691 12.9921 15.8986ZM19.4483 8.35095C19.4483 10.3408 18.5416 12.0938 17.1523 13.1448L15.9642 11.9567C17.0152 11.2297 17.7198 9.89788 17.7198 8.35095C17.7198 6.05603 16.0889 4.28845 14.0968 4.28845C12.6116 4.28845 11.3238 5.29692 10.7713 6.7638L9.4938 5.48628C10.4193 3.79272 12.1389 2.65759 14.0968 2.65759C17.0557 2.65759 19.4483 5.17712 19.4483 8.35095Z", fillAlpha = 0.425f)
            addSfPath("M25.503 26.1342C25.8253 26.4662 26.3624 26.4564 26.6846 26.1244C27.0069 25.7924 27.0069 25.2748 26.6846 24.9525L3.58894 1.85681C3.26668 1.53454 2.73933 1.52478 2.4073 1.85681C2.08504 2.16931 2.08504 2.71618 2.4073 3.02868Z", fillAlpha = 0.85f)
        }
        return _sFPersonSlash!!
    }

private var _sFPersonSlash: ImageVector? = null
