package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowClockwise (monochrome)
 * Viewport: 22.4219 x 30.0098
 */
public val SfSymbols.Monochrome.SFArrowClockwise: ImageVector
    get() {
        if (_sFArrowClockwise != null) {
            return _sFArrowClockwise!!
        }
        _sFArrowClockwise = sfIcon(
            name = "Monochrome.SFArrowClockwise",
            viewportWidth = 22.4219f,
            viewportHeight = 30.0098f
        ) {
            addSfPath("M11.0254 26.9434C17.1191 26.9434 22.0605 22.002 22.0605 15.8984C22.0605 15.4297 21.6699 15.0391 21.1914 15.0391C20.7227 15.0391 20.332 15.4297 20.332 15.8984C20.332 21.0352 16.1621 25.2051 11.0254 25.2051C5.88867 25.2051 1.72852 21.0352 1.72852 15.8984C1.72852 10.7617 5.88867 6.60156 11.0254 6.60156C12.2266 6.60156 13.3691 6.82617 14.4141 7.24609C14.9707 7.46094 15.6055 7.14844 15.625 6.50391C15.6445 5.9668 15.2637 5.74219 14.9512 5.625C13.7695 5.15625 12.4316 4.88281 11.0254 4.88281C4.93164 4.88281 0 9.81445 0 15.9082C0 22.002 4.93164 26.9434 11.0254 26.9434ZM14.6484 6.34766L9.82422 11.1621C9.64844 11.3281 9.58008 11.543 9.58008 11.7773C9.58008 12.2656 9.94141 12.6465 10.4297 12.6465C10.6934 12.6465 10.8887 12.5488 11.0547 12.3926L16.3574 7.04102C16.5723 6.80664 16.6602 6.5918 16.6602 6.34766C16.6602 6.11328 16.5527 5.86914 16.3574 5.66406L11.0547 0.273438C10.8887 0.107422 10.6934 0 10.4199 0C9.94141 0 9.58008 0.400391 9.58008 0.888672C9.58008 1.11328 9.64844 1.34766 9.81445 1.51367Z", fillAlpha = 0.85f)
        }
        return _sFArrowClockwise!!
    }

private var _sFArrowClockwise: ImageVector? = null
