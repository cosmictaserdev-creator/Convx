package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFPlusAppFill: ImageVector
    get() {
        if (_sFPlusAppFill != null) {
            return _sFPlusAppFill!!
        }
        _sFPlusAppFill = sfIcon(
            name = "Dualtone.SFPlusAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M12.373 16.3965L12.373 6.46484C12.373 5.89844 11.9824 5.50781 11.4258 5.50781C10.8887 5.50781 10.5078 5.9082 10.5078 6.46484L10.5078 16.3965C10.5078 16.9434 10.8887 17.3438 11.4258 17.3438C11.9824 17.3438 12.373 16.9531 12.373 16.3965ZM6.47461 12.3535L16.416 12.3535C16.9629 12.3535 17.3633 11.9824 17.3633 11.4453C17.3633 10.8789 16.9629 10.498 16.416 10.498L6.47461 10.498C5.91797 10.498 5.52734 10.8789 5.52734 11.4453C5.52734 11.9824 5.92773 12.3535 6.47461 12.3535Z", fillAlpha = 0.85f)
        }
        return _sFPlusAppFill!!
    }

private var _sFPlusAppFill: ImageVector? = null
