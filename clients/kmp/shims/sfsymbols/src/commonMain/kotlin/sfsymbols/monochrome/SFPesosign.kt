package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPesosign (monochrome)
 * Viewport: 19.082 x 22.7637
 */
public val SfSymbols.Monochrome.SFPesosign: ImageVector
    get() {
        if (_sFPesosign != null) {
            return _sFPesosign!!
        }
        _sFPesosign = sfIcon(
            name = "Monochrome.SFPesosign",
            viewportWidth = 19.082f,
            viewportHeight = 22.7637f
        ) {
            addSfPath("M3.44727 22.7246C4.0625 22.7246 4.50195 22.2656 4.50195 21.6504L4.50195 14.541L9.38477 14.541C13.7207 14.541 16.709 11.5625 16.709 7.25586C16.709 3.01758 13.8184 0 9.4043 0L3.4375 0C2.8125 0 2.37305 0.458984 2.37305 1.10352L2.37305 21.6504C2.37305 22.2656 2.83203 22.7246 3.44727 22.7246ZM4.50195 12.6855L4.50195 1.8457L8.93555 1.8457C12.4414 1.8457 14.5605 3.86719 14.5605 7.25586C14.5605 10.6738 12.4121 12.6855 8.93555 12.6855ZM0 5.29297C0 5.71289 0.302734 6.00586 0.722656 6.00586L17.998 6.00586C18.418 6.00586 18.7207 5.70312 18.7207 5.29297C18.7207 4.89258 18.418 4.58008 17.998 4.58008L0.722656 4.58008C0.302734 4.58008 0 4.89258 0 5.29297ZM0 9.26758C0 9.6875 0.302734 9.98047 0.722656 9.98047L17.998 9.98047C18.418 9.98047 18.7207 9.67773 18.7207 9.26758C18.7207 8.86719 18.418 8.56445 17.998 8.56445L0.722656 8.56445C0.302734 8.56445 0 8.86719 0 9.26758Z", fillAlpha = 0.85f)
        }
        return _sFPesosign!!
    }

private var _sFPesosign: ImageVector? = null
