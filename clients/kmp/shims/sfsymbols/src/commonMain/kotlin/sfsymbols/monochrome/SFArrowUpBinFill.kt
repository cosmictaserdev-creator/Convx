package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpBinFill (monochrome)
 * Viewport: 30.332 x 22.9785
 */
public val SfSymbols.Monochrome.SFArrowUpBinFill: ImageVector
    get() {
        if (_sFArrowUpBinFill != null) {
            return _sFArrowUpBinFill!!
        }
        _sFArrowUpBinFill = sfIcon(
            name = "Monochrome.SFArrowUpBinFill",
            viewportWidth = 30.332f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M25.2832 19.3359C24.9316 21.6992 23.9355 22.9785 21.543 22.9785L8.42773 22.9785C6.02539 22.9785 5.04883 21.7188 4.6875 19.3359L2.86133 7.25586L27.0996 7.25586ZM14.3945 9.94141L10.6348 13.4668C10.4492 13.6328 10.3223 13.8184 10.3223 14.1016C10.3223 14.5605 10.6543 14.8828 11.1133 14.8828C11.3281 14.8828 11.5527 14.7852 11.7188 14.6289L12.793 13.5254L14.2519 12.0326L14.1602 14.2969L14.1602 19.3848C14.1602 19.8438 14.5508 20.2148 15.0195 20.2148C15.4785 20.2148 15.8691 19.8438 15.8691 19.3848L15.8691 14.2969L15.7845 12.0329L17.2363 13.5254L18.3203 14.6289C18.4766 14.7852 18.6914 14.8828 18.916 14.8828C19.3652 14.8828 19.7168 14.5605 19.7168 14.1016C19.7168 13.8184 19.5898 13.6328 19.4043 13.4668L15.6445 9.94141C15.4102 9.73633 15.2539 9.6582 15.0195 9.6582C14.7852 9.6582 14.6191 9.73633 14.3945 9.94141ZM29.9707 2.17773L29.9707 3.55469C29.9707 4.88281 29.2676 5.70312 27.9199 5.70312L2.04102 5.70312C0.703125 5.70312 0 4.88281 0 3.55469L0 2.17773C0 0.839844 0.761719 0.0292969 2.04102 0.0292969L27.9199 0.0292969C29.2676 0.0292969 29.9707 0.839844 29.9707 2.17773Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpBinFill!!
    }

private var _sFArrowUpBinFill: ImageVector? = null
