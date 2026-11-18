package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppleLogo (monochrome)
 * Viewport: 20.2271 x 26.2402
 */
public val SfSymbols.Monochrome.SFAppleLogo: ImageVector
    get() {
        if (_sFAppleLogo != null) {
            return _sFAppleLogo!!
        }
        _sFAppleLogo = sfIcon(
            name = "Monochrome.SFAppleLogo",
            viewportWidth = 20.2271f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M15.0257 6.00586C12.9847 5.85938 11.2366 7.1582 10.2894 7.1582C9.30302 7.1582 7.78935 6.05469 6.16826 6.08398C4.05888 6.10352 2.13506 7.30469 1.02177 9.18945C-1.14619 12.9883 0.49443 18.6328 2.61357 21.6992C3.63896 23.2324 4.89873 24.9219 6.53935 24.873C8.10185 24.7852 8.68779 23.8379 10.6019 23.8379C12.5159 23.8379 13.0335 24.873 14.6936 24.8242C16.4124 24.7852 17.4573 23.291 18.5218 21.7773C19.7132 20.0098 20.2112 18.3203 20.221 18.2227C20.1722 18.2227 16.93 16.9727 16.8909 13.2129C16.8811 10.0684 19.469 8.54492 19.5667 8.47656C18.1019 6.32812 15.8167 6.08398 15.0257 6.00586ZM13.7561 3.97461C14.6253 2.91016 15.2112 1.46484 15.0647 0C13.805 0.0488281 12.2913 0.839844 11.3929 1.88477C10.6019 2.82227 9.89873 4.31641 10.094 5.73242C11.4905 5.83008 12.9065 5.01953 13.7561 3.97461Z", fillAlpha = 0.85f)
        }
        return _sFAppleLogo!!
    }

private var _sFAppleLogo: ImageVector? = null
