package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeOpen (monochrome)
 * Viewport: 30.498 x 28.9746
 */
public val SfSymbols.Monochrome.SFEnvelopeOpen: ImageVector
    get() {
        if (_sFEnvelopeOpen != null) {
            return _sFEnvelopeOpen!!
        }
        _sFEnvelopeOpen = sfIcon(
            name = "Monochrome.SFEnvelopeOpen",
            viewportWidth = 30.498f,
            viewportHeight = 28.9746f
        ) {
            addSfPath("M3.79883 28.9746L26.6309 28.9746C28.8477 28.9746 30.1367 27.6953 30.1367 25.2148L30.1367 12.2559C30.1367 10.4004 29.8438 9.59961 28.5156 8.70117L17.4023 1.13281C15.6348-0.078125 14.4043-0.078125 12.6367 1.13281L1.62109 8.70117C0.302734 9.59961 0 10.4004 0 12.2559L0 25.2148C0 27.6953 1.29883 28.9746 3.79883 28.9746ZM3.75977 27.3242C2.41211 27.3242 1.65039 26.582 1.65039 25.1953L1.65039 11.9922C1.65039 10.9277 1.8457 10.4492 2.62695 9.92188L13.7598 2.34375C14.6875 1.69922 15.3516 1.69922 16.2891 2.34375L27.5098 9.92188C28.291 10.4395 28.4766 10.9277 28.4766 11.9922L28.4766 25.2148C28.4766 26.582 27.7246 27.3242 26.377 27.3242ZM15.0586 15.4492C14.2773 15.4492 13.5547 15.7422 12.8516 16.3867L0.957031 27.0801L2.08984 28.2129L13.8184 17.6562C14.2285 17.2852 14.6289 17.1094 15.0586 17.1094C15.4883 17.1094 15.8984 17.2852 16.3086 17.6562L28.0273 28.2129L29.1602 27.0801L17.2754 16.3867C16.5723 15.7422 15.8398 15.4492 15.0586 15.4492ZM2.03125 9.61914L0.908203 10.7617L9.99023 19.834L11.123 18.7109ZM28.1055 9.60938L19.0039 18.7109L20.1367 19.834L29.2383 10.752Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeOpen!!
    }

private var _sFEnvelopeOpen: ImageVector? = null
