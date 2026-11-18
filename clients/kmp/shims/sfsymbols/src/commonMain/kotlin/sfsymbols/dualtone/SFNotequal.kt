package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNotequal (dualtone)
 * Viewport: 17.6367 x 17.5736
 */
public val SfSymbols.Dualtone.SFNotequal: ImageVector
    get() {
        if (_sFNotequal != null) {
            return _sFNotequal!!
        }
        _sFNotequal = sfIcon(
            name = "Dualtone.SFNotequal",
            viewportWidth = 17.6367f,
            viewportHeight = 17.5736f
        ) {
            addSfPath("M2.33398 17.3464C2.80273 17.6784 3.39844 17.571 3.74023 17.1022L15.1562 1.54561C15.4785 1.10616 15.4102 0.529983 14.9316 0.207718C14.4629-0.114548 13.9551-0.085251 13.5449 0.461624L2.1875 15.9499C1.83594 16.4382 1.82617 17.0046 2.33398 17.3464ZM0.957031 5.79366L16.3281 5.79366C16.8359 5.79366 17.2754 5.36397 17.2754 4.84639C17.2754 4.31905 16.8359 3.88936 16.3281 3.88936L0.957031 3.88936C0.439453 3.88936 0 4.31905 0 4.84639C0 5.36397 0.439453 5.79366 0.957031 5.79366ZM0.957031 13.7136L16.3281 13.7136C16.8359 13.7136 17.2754 13.2839 17.2754 12.7565C17.2754 12.239 16.8359 11.8093 16.3281 11.8093L0.957031 11.8093C0.439453 11.8093 0 12.239 0 12.7565C0 13.2839 0.439453 13.7136 0.957031 13.7136Z", fillAlpha = 0.85f)
        }
        return _sFNotequal!!
    }

private var _sFNotequal: ImageVector? = null
