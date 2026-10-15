package com.birtucansoftware.birtusince.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.R

/**
 * Lexend Font Family - loaded locally from res/font/lexend.ttf
 */
val LexendFontFamily = FontFamily(
    Font(R.font.lexend, FontWeight.Normal),
    Font(R.font.lexend, FontWeight.Medium),
    Font(R.font.lexend, FontWeight.SemiBold),
    Font(R.font.lexend, FontWeight.Bold)
)

/**
 * Tabular Figures ("tnum") font feature string to prevent digit jitter in timers
 */
const val TabularFontFeature = "tnum"

/**
 * BirtuSince Typography scale as specified in Section 4 of UI Spec:
 * - Timer XL: 52 / 56 sp, 600, tracking -1%
 * - Timer M: 22 / 26 sp, 600, tracking 0
 * - Title L: 24 / 30 sp, 600, tracking 0
 * - Title M: 17 / 22 sp, 500, tracking 0
 * - Body: 16 / 24 sp, 400, tracking 0
 * - Label: 14 / 20 sp, 500, tracking +1%
 * - Caption: 12 / 16 sp, 400, tracking +2%
 */
object BirtuTypography {
    val timerXL = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 52.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.52).sp,
        fontFeatureSettings = TabularFontFeature
    )

    val timerM = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        fontFeatureSettings = TabularFontFeature
    )

    val titleL = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    )

    val titleM = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    val body = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

    val label = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.14.sp
    )

    val caption = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.24.sp
    )
}
