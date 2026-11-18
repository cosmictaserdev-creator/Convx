package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanorequalto (monochrome)
 * Viewport: 15.625 x 20.8887
 */
public val SfSymbols.Monochrome.SFLessthanorequalto: ImageVector
    get() {
        if (_sFLessthanorequalto != null) {
            return _sFLessthanorequalto!!
        }
        _sFLessthanorequalto = sfIcon(
            name = "Monochrome.SFLessthanorequalto",
            viewportWidth = 15.625f,
            viewportHeight = 20.8887f
        ) {
            addSfPath("M0 19.9316C0 20.4492 0.439453 20.8887 0.957031 20.8887L14.3066 20.8887C14.8242 20.8887 15.2637 20.4492 15.2637 19.9316C15.2637 19.4043 14.8242 18.9746 14.3066 18.9746L0.957031 18.9746C0.439453 18.9746 0 19.4043 0 19.9316ZM13.7109 14.6484C14.2383 14.6484 14.6191 14.2773 14.6191 13.6914C14.6191 13.2031 14.3555 12.9297 13.9258 12.7344L2.53906 7.38281L2.53906 7.25586L13.9258 1.97266C14.3652 1.77734 14.6191 1.49414 14.6191 0.996094C14.6191 0.429688 14.2285 0.0488281 13.6719 0.0488281C13.3789 0.0488281 13.1738 0.136719 12.9688 0.234375L1.07422 5.99609C0.458984 6.2793 0 6.71875 0 7.39258C0 8.06641 0.458984 8.49609 1.06445 8.7793L12.9688 14.4629C13.1934 14.5703 13.3984 14.6484 13.7109 14.6484Z", fillAlpha = 0.85f)
        }
        return _sFLessthanorequalto!!
    }

private var _sFLessthanorequalto: ImageVector? = null
