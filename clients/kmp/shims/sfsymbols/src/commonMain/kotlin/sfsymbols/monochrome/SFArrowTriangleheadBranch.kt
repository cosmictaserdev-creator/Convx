package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTriangleheadBranch (monochrome)
 * Viewport: 23.1406 x 24.1895
 */
public val SfSymbols.Monochrome.SFArrowTriangleheadBranch: ImageVector
    get() {
        if (_sFArrowTriangleheadBranch != null) {
            return _sFArrowTriangleheadBranch!!
        }
        _sFArrowTriangleheadBranch = sfIcon(
            name = "Monochrome.SFArrowTriangleheadBranch",
            viewportWidth = 23.1406f,
            viewportHeight = 24.1895f
        ) {
            addSfPath("M2.92282 6.8457L6.26267 2.23633C6.72165 1.60156 6.40915 1.09375 5.64743 1.08398L0.813449 1.00586C0.178683 0.996094-0.143582 1.43555 0.061496 2.04102L1.624 6.60156C1.8779 7.33398 2.44431 7.48047 2.92282 6.8457ZM10.4717 23.2715C10.4717 23.7793 10.8916 24.1895 11.3896 24.1895C11.8974 24.1895 12.3076 23.7793 12.3076 23.2715L12.3076 16.0352C12.3076 10.4688 9.24118 5.73242 3.86032 2.85156C3.30368 2.53906 2.80564 2.8418 2.60056 3.27148C2.41501 3.67188 2.49314 4.17969 2.97165 4.45312C7.82517 7.04102 10.4717 11.1816 10.4717 16.0352ZM19.8564 6.8457C20.3252 7.48047 20.9013 7.33398 21.1455 6.60156L22.708 2.04102C22.9228 1.43555 22.5908 0.996094 21.9658 1.00586L17.1318 1.08398C16.3603 1.09375 16.0478 1.60156 16.5166 2.23633ZM12.3076 23.2715L12.3076 16.0352C12.3076 11.1816 14.9541 7.04102 19.8076 4.45312C20.2861 4.17969 20.3642 3.67188 20.1787 3.27148C19.9736 2.8418 19.4756 2.53906 18.9189 2.85156C13.5381 5.73242 10.4717 10.4688 10.4717 16.0352L10.4717 23.2715C10.4717 23.7793 10.8916 24.1895 11.3896 24.1895C11.8974 24.1895 12.3076 23.7793 12.3076 23.2715Z", fillAlpha = 0.85f)
        }
        return _sFArrowTriangleheadBranch!!
    }

private var _sFArrowTriangleheadBranch: ImageVector? = null
