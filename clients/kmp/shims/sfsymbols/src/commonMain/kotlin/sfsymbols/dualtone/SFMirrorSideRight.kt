package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMirrorSideRight (dualtone)
 * Viewport: 35.3516 x 19.2676
 */
public val SfSymbols.Dualtone.SFMirrorSideRight: ImageVector
    get() {
        if (_sFMirrorSideRight != null) {
            return _sFMirrorSideRight!!
        }
        _sFMirrorSideRight = sfIcon(
            name = "Dualtone.SFMirrorSideRight",
            viewportWidth = 35.3516f,
            viewportHeight = 19.2676f
        ) {
            addSfPath("M34.9902 10.332C34.9902 4.74609 31.7969 1.36719 25.9473 0.634766C21.8848 0.146484 14.6191 0.00976562 10.498 0C7.25586 0 5.56641 1.78711 5.86914 4.72656L6.25977 8.55469C6.37695 9.58008 5.83984 10.0684 4.72656 10.0684L1.31836 10.0684C0.546875 10.0684 0 10.625 0 11.3965L0 17.5684C0 18.7598 0.898438 19.2383 2.06055 18.9355C3.75 18.5254 5.42969 18.3203 7.40234 18.3203L26.5918 18.3203C31.8457 18.3203 34.9902 15.4688 34.9902 10.332ZM33.2617 10.332C33.2617 14.3457 30.7422 16.5918 26.6309 16.5918L12.2363 16.5918C9.82422 16.5918 8.84766 15.7715 8.59375 13.5547L7.57812 4.52148C7.37305 2.68555 8.32031 1.72852 10.498 1.72852C14.6094 1.72852 21.7773 1.9043 25.8008 2.37305C30.6738 2.95898 33.2617 5.73242 33.2617 10.332Z", fillAlpha = 0.85f)
            addSfPath("M33.2617 10.332C33.2617 14.3457 30.7422 16.5918 26.6309 16.5918L12.2363 16.5918C9.82422 16.5918 8.84766 15.7715 8.59375 13.5547L7.57812 4.52148C7.37305 2.68555 8.32031 1.72852 10.498 1.72852C14.6094 1.72852 21.7773 1.9043 25.8008 2.37305C30.6738 2.95898 33.2617 5.73242 33.2617 10.332Z", fillAlpha = 0.2125f)
        }
        return _sFMirrorSideRight!!
    }

private var _sFMirrorSideRight: ImageVector? = null
