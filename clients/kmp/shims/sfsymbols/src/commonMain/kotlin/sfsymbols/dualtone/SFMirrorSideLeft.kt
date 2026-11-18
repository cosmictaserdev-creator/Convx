package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMirrorSideLeft (dualtone)
 * Viewport: 35.3516 x 19.2676
 */
public val SfSymbols.Dualtone.SFMirrorSideLeft: ImageVector
    get() {
        if (_sFMirrorSideLeft != null) {
            return _sFMirrorSideLeft!!
        }
        _sFMirrorSideLeft = sfIcon(
            name = "Dualtone.SFMirrorSideLeft",
            viewportWidth = 35.3516f,
            viewportHeight = 19.2676f
        ) {
            addSfPath("M0 10.332C0 15.5469 3.05664 18.3203 8.38867 18.3203L27.5879 18.3203C29.5605 18.3203 31.2305 18.5254 32.9199 18.9355C34.0918 19.2383 34.9902 18.7598 34.9902 17.5684L34.9902 11.3965C34.9902 10.625 34.4434 10.0684 33.6719 10.0684L30.2637 10.0684C29.1504 10.0684 28.6133 9.58008 28.7207 8.55469L29.1211 4.72656C29.4238 1.78711 27.7344 0 24.4922 0C20.3711 0.00976562 13.1055 0.146484 9.04297 0.634766C3.18359 1.36719 0 4.74609 0 10.332ZM1.72852 10.332C1.72852 5.73242 4.31641 2.95898 9.17969 2.37305C13.2031 1.9043 20.3711 1.72852 24.4922 1.72852C26.6699 1.72852 27.6172 2.68555 27.4121 4.52148L26.3965 13.5547C26.1426 15.7715 25.166 16.5918 22.7539 16.5918L8.35938 16.5918C4.16992 16.5918 1.72852 14.4141 1.72852 10.332Z", fillAlpha = 0.85f)
            addSfPath("M1.72852 10.332C1.72852 5.73242 4.31641 2.95898 9.17969 2.37305C13.2031 1.9043 20.3711 1.72852 24.4922 1.72852C26.6699 1.72852 27.6172 2.68555 27.4121 4.52148L26.3965 13.5547C26.1426 15.7715 25.166 16.5918 22.7539 16.5918L8.35938 16.5918C4.16992 16.5918 1.72852 14.4141 1.72852 10.332Z", fillAlpha = 0.2125f)
        }
        return _sFMirrorSideLeft!!
    }

private var _sFMirrorSideLeft: ImageVector? = null
