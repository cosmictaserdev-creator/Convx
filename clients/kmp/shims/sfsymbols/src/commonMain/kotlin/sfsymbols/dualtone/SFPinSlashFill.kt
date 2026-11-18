package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinSlashFill (dualtone)
 * Viewport: 28.0518 x 29.9316
 */
public val SfSymbols.Dualtone.SFPinSlashFill: ImageVector
    get() {
        if (_sFPinSlashFill != null) {
            return _sFPinSlashFill!!
        }
        _sFPinSlashFill = sfIcon(
            name = "Dualtone.SFPinSlashFill",
            viewportWidth = 28.0518f,
            viewportHeight = 29.9316f
        ) {
            addSfPath("M16.1487 20.4102L14.8267 20.4102L14.8267 26.7871C14.8267 28.4863 14.1235 29.9316 13.8501 29.9316C13.5669 29.9316 12.8735 28.4863 12.8735 26.7871L12.8735 20.4102L6.01807 20.4102C4.97314 20.4102 4.30908 19.7656 4.30908 18.7988C4.30908 16.5009 5.86066 14.16 8.49028 12.7584ZM21.6919 2.11914C21.6919 2.4707 21.5454 2.86133 21.2329 3.27148C20.5786 4.14062 19.0649 5.3125 17.3853 6.28906L17.7856 12.1289C21.2622 13.3691 23.3911 16.1133 23.3911 18.7988C23.3911 19.5619 22.9714 20.1242 22.2795 20.3257L10.1786 8.23003L10.3149 6.28906C8.62549 5.32227 7.11182 4.14062 6.45752 3.27148C6.15479 2.86133 6.0083 2.4707 6.0083 2.11914C6.0083 1.40625 6.54541 0.878906 7.37549 0.878906L20.3149 0.878906C21.145 0.878906 21.6919 1.40625 21.6919 2.11914Z", fillAlpha = 0.425f)
            addSfPath("M1.604 3.94531L24.1235 26.4551C24.4556 26.7871 24.9927 26.7871 25.3052 26.4551C25.6274 26.123 25.6274 25.6055 25.3052 25.2832L2.78564 2.77344C2.47314 2.45117 1.93604 2.44141 1.604 2.77344C1.28174 3.08594 1.28174 3.63281 1.604 3.94531Z", fillAlpha = 0.85f)
        }
        return _sFPinSlashFill!!
    }

private var _sFPinSlashFill: ImageVector? = null
