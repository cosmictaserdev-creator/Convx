package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareStack (dualtone)
 * Viewport: 26.3379 x 32.8418
 */
public val SfSymbols.Dualtone.SFSquareStack: ImageVector
    get() {
        if (_sFSquareStack != null) {
            return _sFSquareStack!!
        }
        _sFSquareStack = sfIcon(
            name = "Dualtone.SFSquareStack",
            viewportWidth = 26.3379f,
            viewportHeight = 32.8418f
        ) {
            addSfPath("M18.6363 3.53516L7.34027 3.53516C7.40282 2.64931 7.97728 2.12891 8.91602 2.12891L17.0605 2.12891C17.9993 2.12891 18.5737 2.64931 18.6363 3.53516Z", fillAlpha = 0.85f)
            addSfPath("M21.1564 6.942C20.8094 6.88925 20.4418 6.86523 20.0586 6.86523L5.91797 6.86523C5.53117 6.86523 5.16046 6.88947 4.81065 6.94255C4.9444 5.91296 5.65963 5.33203 6.80664 5.33203L19.1699 5.33203C20.3167 5.33203 21.0241 5.91275 21.1564 6.942Z", fillAlpha = 0.85f)
            addSfPath("M5.91797 30.7324L20.0586 30.7324C22.5781 30.7324 23.8574 29.4531 23.8574 26.9629L23.8574 12.7441C23.8574 10.2637 22.5781 8.98438 20.0586 8.98438L5.91797 8.98438C3.38867 8.98438 2.11914 10.2539 2.11914 12.7441L2.11914 26.9629C2.11914 29.4629 3.38867 30.7324 5.91797 30.7324ZM5.94727 29.0039C4.58984 29.0039 3.84766 28.2715 3.84766 26.875L3.84766 12.8418C3.84766 11.4355 4.58984 10.7129 5.94727 10.7129L20.0293 10.7129C21.3574 10.7129 22.1289 11.4355 22.1289 12.8418L22.1289 26.875C22.1289 28.2715 21.3574 29.0039 20.0293 29.0039Z", fillAlpha = 0.85f)
        }
        return _sFSquareStack!!
    }

private var _sFSquareStack: ImageVector? = null
