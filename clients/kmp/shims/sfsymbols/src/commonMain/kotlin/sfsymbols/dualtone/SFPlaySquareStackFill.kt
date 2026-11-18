package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlaySquareStackFill (dualtone)
 * Viewport: 26.3379 x 32.8418
 */
public val SfSymbols.Dualtone.SFPlaySquareStackFill: ImageVector
    get() {
        if (_sFPlaySquareStackFill != null) {
            return _sFPlaySquareStackFill!!
        }
        _sFPlaySquareStackFill = sfIcon(
            name = "Dualtone.SFPlaySquareStackFill",
            viewportWidth = 26.3379f,
            viewportHeight = 32.8418f
        ) {
            addSfPath("M18.6363 3.53516L7.34027 3.53516C7.40282 2.64931 7.97728 2.12891 8.91602 2.12891L17.0605 2.12891C17.9993 2.12891 18.5737 2.64931 18.6363 3.53516Z", fillAlpha = 0.2125f)
            addSfPath("M21.1564 6.942C20.8094 6.88925 20.4418 6.86523 20.0586 6.86523L5.91797 6.86523C5.53117 6.86523 5.16046 6.88947 4.81065 6.94255C4.9444 5.91296 5.65963 5.33203 6.80664 5.33203L19.1699 5.33203C20.3167 5.33203 21.0241 5.91275 21.1564 6.942Z", fillAlpha = 0.2125f)
            addSfPath("M5.91797 30.7324L20.0586 30.7324C22.5781 30.7324 23.8574 29.4531 23.8574 26.9629L23.8574 12.7441C23.8574 10.2637 22.5781 8.98438 20.0586 8.98438L5.91797 8.98438C3.38867 8.98438 2.11914 10.2539 2.11914 12.7441L2.11914 26.9629C2.11914 29.4629 3.38867 30.7324 5.91797 30.7324Z", fillAlpha = 0.2125f)
            addSfPath("M10.5762 24.9609C10.0195 25.3027 9.36523 25.0195 9.36523 24.4434L9.36523 15.3906C9.36523 14.8145 10.0586 14.5703 10.5762 14.873L17.9395 19.2676C18.4473 19.5703 18.457 20.2832 17.9395 20.5859Z", fillAlpha = 0.85f)
        }
        return _sFPlaySquareStackFill!!
    }

private var _sFPlaySquareStackFill: ImageVector? = null
