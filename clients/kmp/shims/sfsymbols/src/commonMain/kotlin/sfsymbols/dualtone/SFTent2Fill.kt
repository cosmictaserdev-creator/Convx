package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTent2Fill (dualtone)
 * Viewport: 36.2338 x 25.7324
 */
public val SfSymbols.Dualtone.SFTent2Fill: ImageVector
    get() {
        if (_sFTent2Fill != null) {
            return _sFTent2Fill!!
        }
        _sFTent2Fill = sfIcon(
            name = "Dualtone.SFTent2Fill",
            viewportWidth = 36.2338f,
            viewportHeight = 25.7324f
        ) {
            addSfPath("M10.7927 3.45703L15.6807 13.2689L13.1908 18.2642L10.0115 11.4648C9.89426 11.2305 9.70872 11.1133 9.49387 11.1133C9.27903 11.1133 9.08372 11.2305 8.9763 11.4648L5.03098 19.9023L8.02903 19.9023L9.49387 13.4082L10.9587 19.9023L12.3743 19.9023L11.5516 21.5527L1.32005 21.5527C0.118874 21.5527-0.281516 20.4492 0.196999 19.4922L8.19505 3.45703C8.45872 2.92969 8.947 2.58789 9.49387 2.58789C10.031 2.58789 10.5193 2.92969 10.7927 3.45703Z", fillAlpha = 0.425f)
            addSfPath("M11.9353 25.7324L34.3474 25.7324C35.7732 25.7324 36.2029 24.4824 35.6267 23.3496L24.6794 1.37695C24.3572 0.722656 23.7615 0.332031 23.1365 0.332031C22.5115 0.332031 21.9353 0.703125 21.6228 1.34766L10.656 23.3496C10.0896 24.4824 10.5193 25.7324 11.9353 25.7324ZM16.9841 24.0625L22.4138 12.4512C22.5798 12.1191 22.8435 11.9629 23.1365 11.9629C23.4294 11.9629 23.7029 12.1191 23.8591 12.4512L29.2888 24.0625L25.1286 24.0625L23.1365 15.1074L21.1443 24.0625Z", fillAlpha = 0.85f)
        }
        return _sFTent2Fill!!
    }

private var _sFTent2Fill: ImageVector? = null
