package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUp (dualtone)
 * Viewport: 19.2676 x 23.6816
 */
public val SfSymbols.Dualtone.SFArrowUp: ImageVector
    get() {
        if (_sFArrowUp != null) {
            return _sFArrowUp!!
        }
        _sFArrowUp = sfIcon(
            name = "Dualtone.SFArrowUp",
            viewportWidth = 19.2676f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M0.917969 10.3906C1.17188 10.3906 1.42578 10.3027 1.5918 10.1367L5.05859 6.70898L9.44336 1.94336L13.8477 6.70898L17.3145 10.1367C17.4805 10.3027 17.7344 10.3906 17.9883 10.3906C18.5156 10.3906 18.9062 9.99023 18.9062 9.45312C18.9062 9.19922 18.8184 8.98438 18.6133 8.76953L10.166 0.322266C9.96094 0.107422 9.7168 0 9.45312 0C9.18945 0 8.94531 0.107422 8.74023 0.322266L0.292969 8.76953C0.0878906 8.98438 0 9.19922 0 9.45312C0 9.99023 0.390625 10.3906 0.917969 10.3906ZM9.45312 23.6816C10.0098 23.6816 10.4004 23.291 10.4004 22.7441L10.4004 5.56641L10.293 2.03125C10.293 1.5332 9.95117 1.18164 9.45312 1.18164C8.95508 1.18164 8.61328 1.5332 8.61328 2.02148L8.50586 5.56641L8.50586 22.7441C8.50586 23.291 8.89648 23.6816 9.45312 23.6816Z", fillAlpha = 0.85f)
        }
        return _sFArrowUp!!
    }

private var _sFArrowUp: ImageVector? = null
