package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTagCircle: ImageVector
    get() {
        if (_sFTagCircle != null) {
            return _sFTagCircle!!
        }
        _sFTagCircle = sfIcon(
            name = "Monochrome.SFTagCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M10.2637 19.2969C10.9473 19.9902 11.6406 20 12.3633 19.2871L18.5645 13.125C19.0332 12.6758 19.3555 12.3535 19.3555 11.6211L19.3555 9.21875C19.3555 8.67188 19.209 8.35938 18.8379 7.98828L17.7246 6.88477C17.3535 6.52344 17.041 6.29883 16.4844 6.29883L14.0234 6.29883C13.3008 6.29883 12.9688 6.63086 12.5098 7.08984L6.35742 13.291C5.6543 14.0039 5.66406 14.6875 6.34766 15.3711ZM15.0586 10.5859C14.7266 10.2637 14.7266 9.7168 15.0586 9.36523C15.3809 9.0332 15.9375 9.0332 16.2695 9.36523C16.6113 9.69727 16.6016 10.2344 16.2598 10.5762C15.9277 10.9277 15.3906 10.918 15.0586 10.5859Z", fillAlpha = 0.85f)
        }
        return _sFTagCircle!!
    }

private var _sFTagCircle: ImageVector? = null
