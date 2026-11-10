/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package zone.ien.hig

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.White
import zone.ien.hig.theme.isDark

/** Accessibility preferences used by Cupertino components. */
object Accessibility

/** Whether high contrast is enabled. */
val Accessibility.isHighContrastEnabled: Boolean get() = false

/** Whether reduced transparency is enabled. */
val Accessibility.isReduceTransparencyEnabled: Boolean get() = false

/** Applies the current contrast preference to this color. */
val Color.accessible: Color
    @Composable
    get() = accessible(isDark())

/** Applies high contrast when it is enabled. */
fun Color.accessible(isDark: Boolean): Color =
    if (Accessibility.isHighContrastEnabled) {
        lerp(this, if (isDark) CupertinoColors.White else Color.Black, .2f)
    } else {
        this
    }
