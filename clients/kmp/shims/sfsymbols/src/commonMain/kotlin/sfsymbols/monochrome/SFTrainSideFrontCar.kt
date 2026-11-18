package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrainSideFrontCar (monochrome)
 * Viewport: 34.5703 x 19.7363
 */
public val SfSymbols.Monochrome.SFTrainSideFrontCar: ImageVector
    get() {
        if (_sFTrainSideFrontCar != null) {
            return _sFTrainSideFrontCar!!
        }
        _sFTrainSideFrontCar = sfIcon(
            name = "Monochrome.SFTrainSideFrontCar",
            viewportWidth = 34.5703f,
            viewportHeight = 19.7363f
        ) {
            addSfPath("M0 16.0645C0 18.4961 1.24023 19.7363 3.71094 19.7363L28.0859 19.7363C32.0898 19.7363 34.209 17.3145 34.209 14.502C34.209 12.8027 33.4863 10.9863 31.8945 9.4043L26.582 4.14062C23.3594 0.957031 21.2793 0 17.4707 0L3.71094 0C1.24023 0 0 1.24023 0 3.67188ZM13.1543 10.0488L13.1543 5.22461C13.1543 4.50195 13.584 4.08203 14.3164 4.08203L17.9883 4.08203C21.3867 4.08203 23.1348 4.51172 24.9609 6.32812L28.0762 9.42383C28.8086 10.1562 28.3984 11.1719 27.5781 11.1719L14.3164 11.1719C13.584 11.1719 13.1543 10.752 13.1543 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFTrainSideFrontCar!!
    }

private var _sFTrainSideFrontCar: ImageVector? = null
