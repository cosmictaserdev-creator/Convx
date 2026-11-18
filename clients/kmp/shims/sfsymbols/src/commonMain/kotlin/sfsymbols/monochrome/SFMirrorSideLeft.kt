package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMirrorSideLeft (monochrome)
 * Viewport: 35.3516 x 19.2676
 */
public val SfSymbols.Monochrome.SFMirrorSideLeft: ImageVector
    get() {
        if (_sFMirrorSideLeft != null) {
            return _sFMirrorSideLeft!!
        }
        _sFMirrorSideLeft = sfIcon(
            name = "Monochrome.SFMirrorSideLeft",
            viewportWidth = 35.3516f,
            viewportHeight = 19.2676f
        ) {
            addSfPath("M29.1211 4.72656L28.7207 8.55469C28.6133 9.58008 29.1504 10.0684 30.2637 10.0684L33.6719 10.0684C34.4434 10.0684 34.9902 10.625 34.9902 11.3965L34.9902 17.5684C34.9902 18.7598 34.0918 19.2383 32.9199 18.9355C31.2305 18.5254 29.5605 18.3203 27.5879 18.3203L8.38867 18.3203C3.05664 18.3203 0 15.5469 0 10.332C0 4.74609 3.18359 1.36719 9.04297 0.634766C13.1055 0.146484 20.3711 0.00976562 24.4922 0C27.7344 0 29.4238 1.78711 29.1211 4.72656ZM9.17969 2.37305C4.31641 2.95898 1.72852 5.73242 1.72852 10.332C1.72852 14.4141 4.16992 16.5918 8.35938 16.5918L22.7539 16.5918C25.166 16.5918 26.1426 15.7715 26.3965 13.5547L27.4121 4.52148C27.6172 2.68555 26.6699 1.72852 24.4922 1.72852C20.3711 1.72852 13.2031 1.9043 9.17969 2.37305Z", fillAlpha = 0.85f)
        }
        return _sFMirrorSideLeft!!
    }

private var _sFMirrorSideLeft: ImageVector? = null
