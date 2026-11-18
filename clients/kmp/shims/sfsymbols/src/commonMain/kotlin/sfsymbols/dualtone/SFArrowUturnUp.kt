package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUturnUp (dualtone)
 * Viewport: 23.5352 x 23.9355
 */
public val SfSymbols.Dualtone.SFArrowUturnUp: ImageVector
    get() {
        if (_sFArrowUturnUp != null) {
            return _sFArrowUturnUp!!
        }
        _sFArrowUturnUp = sfIcon(
            name = "Dualtone.SFArrowUturnUp",
            viewportWidth = 23.5352f,
            viewportHeight = 23.9355f
        ) {
            addSfPath("M14.7852 0.126953C14.5215 0.126953 14.2773 0.224609 14.0723 0.439453L6.68945 7.91992C6.50391 8.10547 6.39648 8.37891 6.39648 8.61328C6.39648 9.17969 6.78711 9.55078 7.33398 9.55078C7.60742 9.55078 7.82227 9.46289 7.98828 9.29688L11.7676 5.43945L14.7852 2.01172L17.793 5.43945L21.5723 9.29688C21.748 9.46289 21.9629 9.55078 22.2363 9.55078C22.7832 9.55078 23.1738 9.17969 23.1738 8.61328C23.1738 8.37891 23.0566 8.10547 22.8809 7.91992L15.4883 0.439453C15.293 0.224609 15.0488 0.126953 14.7852 0.126953ZM0.947266 11.1816C0.429688 11.1816 0 11.5723 0 12.1484L0 15.4102C0 20.8008 3.13477 23.9355 7.82227 23.9355C12.5195 23.9355 15.7324 20.7324 15.7324 15.2539L15.7324 5.88867L15.5762 1.94336C15.5566 1.50391 15.2246 1.15234 14.7852 1.15234C14.3359 1.15234 14.0039 1.50391 13.9844 1.94336L13.8379 5.88867L13.8379 15.4004C13.8379 19.6582 11.416 22.041 7.91992 22.041C4.43359 22.041 1.89453 19.6582 1.89453 15.4004L1.89453 12.1484C1.89453 11.5625 1.47461 11.1816 0.947266 11.1816Z", fillAlpha = 0.85f)
        }
        return _sFArrowUturnUp!!
    }

private var _sFArrowUturnUp: ImageVector? = null
