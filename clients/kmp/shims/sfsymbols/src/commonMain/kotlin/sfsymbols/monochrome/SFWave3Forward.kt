package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWave3Forward (monochrome)
 * Viewport: 18.0828 x 29.4562
 */
public val SfSymbols.Monochrome.SFWave3Forward: ImageVector
    get() {
        if (_sFWave3Forward != null) {
            return _sFWave3Forward!!
        }
        _sFWave3Forward = sfIcon(
            name = "Monochrome.SFWave3Forward",
            viewportWidth = 18.0828f,
            viewportHeight = 29.4562f
        ) {
            addSfPath("M17.723 14.733C17.723 9.48884 16.1019 4.58649 13.1722 0.475165C12.4008-0.589288 10.8383 0.348212 11.6586 1.47126C14.4125 5.28962 15.8871 9.85993 15.8871 14.733C15.8871 19.5963 14.4027 24.1666 11.6586 27.9849C10.8578 29.0787 12.3715 30.0845 13.1722 28.9713C16.0922 24.8697 17.723 19.9576 17.723 14.733Z", fillAlpha = 0.85f)
            addSfPath("M10.9359 14.733C10.9359 10.8267 9.71522 7.16462 7.48865 4.16657C6.63904 3.02399 5.15467 4.12751 5.95545 5.17243C8.00623 7.89704 9.09998 11.2076 9.09998 14.733C9.09998 18.2584 8.00623 21.5591 5.95545 24.2838C5.15467 25.3287 6.63904 26.4224 7.48865 25.2896C9.71522 22.2916 10.9359 18.6197 10.9359 14.733Z", fillAlpha = 0.85f)
            addSfPath("M4.16834 14.733C4.16834 12.1646 3.28944 9.77204 1.7074 7.92634C0.799201 6.85212-0.480096 8.06306 0.183966 8.91267C1.69764 10.7681 2.3324 12.4771 2.3324 14.733C2.3324 16.9791 1.69764 18.6978 0.193732 20.5533C-0.470331 21.3931 0.818732 22.5748 1.7074 21.5299C3.28944 19.6841 4.16834 17.2916 4.16834 14.733Z", fillAlpha = 0.85f)
        }
        return _sFWave3Forward!!
    }

private var _sFWave3Forward: ImageVector? = null
