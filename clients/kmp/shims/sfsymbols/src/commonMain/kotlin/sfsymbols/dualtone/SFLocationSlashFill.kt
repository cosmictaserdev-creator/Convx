package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSlashFill (dualtone)
 * Viewport: 25.8012 x 26.1084
 */
public val SfSymbols.Dualtone.SFLocationSlashFill: ImageVector
    get() {
        if (_sFLocationSlashFill != null) {
            return _sFLocationSlashFill!!
        }
        _sFLocationSlashFill = sfIcon(
            name = "Dualtone.SFLocationSlashFill",
            viewportWidth = 25.8012f,
            viewportHeight = 26.1084f
        ) {
            addSfPath("M16.6497 18.207L13.9943 23.8989C13.2619 25.4614 11.2697 25.1001 11.2697 23.4497L11.2404 13.9673C11.2404 13.8013 11.1818 13.7329 11.0158 13.7329L1.46505 13.6939C-0.214636 13.6841-0.517371 11.7017 0.908411 11.0474L6.76265 8.32507ZM23.5158 3.48879L18.6262 13.9702L11.0072 6.35127L21.426 1.50637C22.9787 0.783712 24.2678 1.84817 23.5158 3.48879Z", fillAlpha = 0.425f)
            addSfPath("M22.3244 21.9458C22.6467 22.2779 23.1838 22.2779 23.4963 21.9458C23.8088 21.6333 23.8283 21.0962 23.4963 20.7739L4.32638 1.60402C4.01388 1.29152 3.467 1.28176 3.14474 1.60402C2.82247 1.91652 2.82247 2.4634 3.14474 2.78567Z", fillAlpha = 0.85f)
        }
        return _sFLocationSlashFill!!
    }

private var _sFLocationSlashFill: ImageVector? = null
