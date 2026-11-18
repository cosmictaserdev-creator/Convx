package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationApp (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFLocationApp: ImageVector
    get() {
        if (_sFLocationApp != null) {
            return _sFLocationApp!!
        }
        _sFLocationApp = sfIcon(
            name = "Dualtone.SFLocationApp",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004ZM6.81641 21.1719C5.15625 21.1719 3.87695 20.7227 3.02734 19.8828C2.16797 19.0332 1.72852 17.7539 1.72852 16.084L1.72852 6.81641C1.72852 5.14648 2.16797 3.86719 3.02734 3.01758C3.86719 2.1875 5.15625 1.72852 6.81641 1.72852L16.0938 1.72852C17.7637 1.72852 19.0332 2.17773 19.8828 3.01758C20.7422 3.86719 21.1816 5.14648 21.1816 6.81641L21.1816 16.084C21.1816 17.7539 20.7422 19.0332 19.8828 19.8828C19.043 20.7129 17.7637 21.1719 16.0938 21.1719Z", fillAlpha = 0.425f)
            addSfPath("M4.79492 12.1875L10.332 12.1973C10.5371 12.1973 10.6641 12.3242 10.6641 12.5391L10.6738 18.0078C10.6738 19.0039 11.7383 19.1016 12.0703 18.3789L17.7734 6.24023C18.1934 5.36133 17.4512 4.63867 16.5723 5.04883L4.44336 10.791C3.70117 11.1426 3.83789 12.1875 4.79492 12.1875Z", fillAlpha = 0.85f)
        }
        return _sFLocationApp!!
    }

private var _sFLocationApp: ImageVector? = null
