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



package zone.ien.hig.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.InternalCupertinoApi

/**
 * Shapes for the Cupertino design system.
 *
 * @param extraSmall the extra small corner shape
 * @param small the small corner shape
 * @param medium the medium corner shape
 * @param large the large corner shape
 * @param extraLarge the extra large corner shape
 */
@Stable
class Shapes(
    val extraSmall: ContinuousRoundedRectangle = ShapeDefaults.ExtraSmall,
    val small: ContinuousRoundedRectangle = ShapeDefaults.Small,
    val medium: ContinuousRoundedRectangle = ShapeDefaults.Medium,
    val large: ContinuousRoundedRectangle = ShapeDefaults.Large,
    val extraLarge: ContinuousRoundedRectangle = ShapeDefaults.ExtraLarge,
) {
    /**
     * Creates a copy of this Shapes with the specified values replaced.
     *
     * @param extraSmall the new extra small corner shape
     * @param small the new small corner shape
     * @param medium the new medium corner shape
     * @param large the new large corner shape
     * @param extraLarge the new extra large corner shape
     */
    fun copy(
        extraSmall: ContinuousRoundedRectangle = this.extraSmall,
        small: ContinuousRoundedRectangle = this.small,
        medium: ContinuousRoundedRectangle = this.medium,
        large: ContinuousRoundedRectangle = this.large,
        extraLarge: ContinuousRoundedRectangle = this.extraLarge,
    ) = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
    )
}

@InternalCupertinoApi
val LocalShapes = staticCompositionLocalOf { Shapes() }

/**
 * Default shapes for the Cupertino design system.
 */
@Immutable
object ShapeDefaults {
    /** Extra small sized corner shape */
    val ExtraSmall: ContinuousRoundedRectangle = ContinuousRoundedRectangle(4.dp)

    /** Small sized corner shape */
    val Small: ContinuousRoundedRectangle = ContinuousRoundedRectangle(8.dp)

    /** Medium sized corner shape */
    val Medium: ContinuousRoundedRectangle = ContinuousRoundedRectangle(12.dp)

    /** Large sized corner shape */
    val Large: ContinuousRoundedRectangle = ContinuousRoundedRectangle(16.dp)

    /** Extra large sized corner shape */
    val ExtraLarge: ContinuousRoundedRectangle = ContinuousRoundedRectangle(24.dp)
}
