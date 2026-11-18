package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMirrorSideRight (monochrome)
 * Viewport: 35.3516 x 19.2676
 */
public val SfSymbols.Monochrome.SFMirrorSideRight: ImageVector
    get() {
        if (_sFMirrorSideRight != null) {
            return _sFMirrorSideRight!!
        }
        _sFMirrorSideRight = sfIcon(
            name = "Monochrome.SFMirrorSideRight",
            viewportWidth = 35.3516f,
            viewportHeight = 19.2676f
        ) {
            addSfPath("M25.9473 0.634766C31.7969 1.36719 34.9902 4.74609 34.9902 10.332C34.9902 15.4688 31.8457 18.3203 26.5918 18.3203L7.40234 18.3203C5.42969 18.3203 3.75 18.5254 2.06055 18.9355C0.898438 19.2383 0 18.7598 0 17.5684L0 11.3965C0 10.625 0.546875 10.0684 1.31836 10.0684L4.72656 10.0684C5.83984 10.0684 6.37695 9.58008 6.25977 8.55469L5.86914 4.72656C5.56641 1.78711 7.25586 0 10.498 0C14.6191 0.00976562 21.8848 0.146484 25.9473 0.634766ZM7.57812 4.52148L8.59375 13.5547C8.84766 15.7715 9.82422 16.5918 12.2363 16.5918L26.6309 16.5918C30.7422 16.5918 33.2617 14.3457 33.2617 10.332C33.2617 5.73242 30.6738 2.95898 25.8008 2.37305C21.7773 1.9043 14.6094 1.72852 10.498 1.72852C8.32031 1.72852 7.37305 2.68555 7.57812 4.52148Z", fillAlpha = 0.85f)
        }
        return _sFMirrorSideRight!!
    }

private var _sFMirrorSideRight: ImageVector? = null
