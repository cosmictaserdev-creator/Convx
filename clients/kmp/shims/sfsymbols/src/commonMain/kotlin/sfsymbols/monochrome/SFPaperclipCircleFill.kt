package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperclipCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPaperclipCircleFill: ImageVector
    get() {
        if (_sFPaperclipCircleFill != null) {
            return _sFPaperclipCircleFill!!
        }
        _sFPaperclipCircleFill = sfIcon(
            name = "Monochrome.SFPaperclipCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM14.2871 6.06445L7.16797 13.1836C5.43945 14.9023 5.43945 17.4512 7.12891 19.1211C8.90625 20.8594 11.3379 20.8105 13.0664 19.1016L18.3691 13.7988C18.6133 13.5645 18.6328 13.1152 18.3789 12.8809C18.1152 12.6367 17.6953 12.6465 17.4414 12.9004L12.1875 18.1543C10.9766 19.3555 9.26758 19.3555 8.08594 18.1836C6.875 17.002 6.9043 15.3027 8.11523 14.0723L15.1953 6.99219C15.9668 6.21094 17.0508 6.19141 17.7539 6.88477C18.4668 7.58789 18.4277 8.64258 17.6367 9.44336L10.7129 16.3574C10.4395 16.6309 10.1367 16.6309 9.87305 16.3965C9.61914 16.1523 9.61914 15.8105 9.89258 15.5371L14.7949 10.6445C15.0684 10.3809 15.0879 10 14.8145 9.73633C14.541 9.48242 14.1699 9.49219 13.9062 9.75586L9.00391 14.668C8.25195 15.4199 8.21289 16.6016 8.93555 17.334C9.66797 18.0859 10.791 18.0566 11.582 17.2656L18.5352 10.3125C19.8535 8.99414 19.8926 7.20703 18.6523 5.9668C17.4023 4.73633 15.5957 4.75586 14.2871 6.06445Z", fillAlpha = 0.85f)
        }
        return _sFPaperclipCircleFill!!
    }

private var _sFPaperclipCircleFill: ImageVector? = null
