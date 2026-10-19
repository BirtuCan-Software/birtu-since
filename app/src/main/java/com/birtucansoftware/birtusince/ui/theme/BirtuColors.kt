package com.birtucansoftware.birtusince.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom BirtuSince color token contract strictly adhering to UI Spec and
 * Neo-tactile design system with complete, clean Light and Dark variants for all presets.
 */
@Immutable
data class BirtuColors(
    val bg: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val text: Color,
    val text2: Color,
    val primary: Color,
    val onPrimary: Color,
    val primarySoft: Color,
    val primaryStrong: Color,
    val late: Color,
    val onLate: Color,
    val scrim: Color,
    val isDark: Boolean = false
) {
    val rose: Color
        get() = if (isDark) Color(0xFFF43F5E) else Color(0xFFE11D48)

    val roseSoft: Color
        get() = if (isDark) Color(0xFF2E1922) else Color(0xFFFFE4E6)

    val roseStrong: Color
        get() = if (isDark) Color(0xFFFF6289) else Color(0xFFBE123C)
}

enum class ThemePreset {
    Tangelo,  // Bright Orange Tangelo & Warm Slate (Default)
    Arctic,   // Glacial Mist & Cyan
    Obsidian, // Velvet Amethyst Quartz
    Amber,    // Warm Honey Amber & Golden Slate
    Emerald,  // Emerald Green & Forest Mint
    Bubblegum // Vibrant Berry Pink & Cotton Candy
}

// -------------------------------------------------------------
// PRESET 1: TANGELO (Bright Orange Tangelo - Default)
// -------------------------------------------------------------
val TangeloLightColors = BirtuColors(
    bg = Color(0xFFFAF7F5),         // Soft warm off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces for cards
    surfaceRaised = Color(0xFFFFF7F2),// Subtle warm raised container
    surfaceHigh = Color(0xFFFEEFE6),// Soft warm high container
    outline = Color(0xFFF3E2D7),    // Delicate warm hairline border
    text = Color(0xFF1E130D),       // Crisp dark ink text
    text2 = Color(0xFF785E50),      // Muted warm terracotta gray
    primary = Color(0xFFFF5E00),    // Vibrant Tangelo orange
    onPrimary = Color(0xFFFFFFFF),  // White text on orange
    primarySoft = Color(0xFFFFEBDD),// Soft orange chip background
    primaryStrong = Color(0xFFE04A00),// Deep tangelo accent
    late = Color(0xFFE11D48),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x661E130D),
    isDark = false
)

val TangeloDarkColors = BirtuColors(
    bg = Color(0xFF0C0E12),         // Deep coal slate
    surface = Color(0xFF151821),    // Muted dark slate card
    surfaceRaised = Color(0xFF1C202C),// Slightly raised slate
    surfaceHigh = Color(0xFF252A3A),// Subtle high container
    outline = Color(0xFF2E3548),    // Soft hairline border
    text = Color(0xFFF8FAFC),       // Crisp white
    text2 = Color(0xFF94A3B8),      // Cool slate gray
    primary = Color(0xFFFF6B35),    // Tangelo orange accent
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFF2B1C16),// Subtle warm tint
    primaryStrong = Color(0xFFFF8552),
    late = Color(0xFFF43F5E),
    onLate = Color(0xFF2B0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val TangeloColors = TangeloLightColors

// -------------------------------------------------------------
// PRESET 2: ARCTIC (Glacial Mist & Cyan)
// -------------------------------------------------------------
val ArcticLightColors = BirtuColors(
    bg = Color(0xFFF4F8FA),         // Soft cyan off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces
    surfaceRaised = Color(0xFFF0F6F9),// Subtle cool raised container
    surfaceHigh = Color(0xFFE2EFF5),// Soft cyan container
    outline = Color(0xFFD4E3EB),    // Soft hairline outline
    text = Color(0xFF0F1E28),       // Deep navy text
    text2 = Color(0xFF5A7282),      // Muted slate text
    primary = Color(0xFF0284C7),    // Glacier cyan blue
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFE0F2FE),// Soft sky chip tint
    primaryStrong = Color(0xFF0369A1),
    late = Color(0xFFE11D48),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x660F1E28),
    isDark = false
)

val ArcticDarkColors = BirtuColors(
    bg = Color(0xFF0A0D14),         // Deep cold coal
    surface = Color(0xFF121722),    // Muted dark glacier card
    surfaceRaised = Color(0xFF1A202E),// Slightly raised cool slate
    surfaceHigh = Color(0xFF222B3D),// Subtle cool container
    outline = Color(0xFF2A364C),    // Soft cool outline
    text = Color(0xFFF0F6FC),       // Crisp off-white
    text2 = Color(0xFF8BA2C4),      // Cool slate
    primary = Color(0xFF00E5FF),    // Glacier cyan accent
    onPrimary = Color(0xFF03222B),
    primarySoft = Color(0xFF0F2633),// Subtle cyan tint
    primaryStrong = Color(0xFF4EEBFF),
    late = Color(0xFFF43F5E),
    onLate = Color(0xFF2B0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val ArcticColors = ArcticLightColors

// -------------------------------------------------------------
// PRESET 3: OBSIDIAN (Velvet Amethyst Quartz)
// -------------------------------------------------------------
val ObsidianLightColors = BirtuColors(
    bg = Color(0xFFF8F7FA),         // Soft quartz off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces
    surfaceRaised = Color(0xFFF5F2F9),// Subtle violet raised container
    surfaceHigh = Color(0xFFEAE4F2),// Soft lavender container
    outline = Color(0xFFDDD5E8),    // Soft lavender outline
    text = Color(0xFF1B1626),       // Deep violet ink text
    text2 = Color(0xFF685E78),      // Muted amethyst text
    primary = Color(0xFF7E42D9),    // Royal Amethyst
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFF0E7FE),// Soft lavender chip tint
    primaryStrong = Color(0xFF6523C2),
    late = Color(0xFFC9304D),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x661B1626),
    isDark = false
)

val ObsidianDarkColors = BirtuColors(
    bg = Color(0xFF0D0B12),         // Deep quartz black
    surface = Color(0xFF15121D),    // Muted dark quartz card
    surfaceRaised = Color(0xFF1D1A28),// Slightly raised quartz slate
    surfaceHigh = Color(0xFF262235),// Subtle quartz container
    outline = Color(0xFF322C44),    // Soft quartz outline
    text = Color(0xFFECE8FA),       // Crisp off-white
    text2 = Color(0xFFA39FBF),      // Muted amethyst
    primary = Color(0xFFB79CFF),    // Amethyst purple accent
    onPrimary = Color(0xFF1C1038),
    primarySoft = Color(0xFF251E38),// Subtle purple tint
    primaryStrong = Color(0xFFC7B3FF),
    late = Color(0xFFFF7A8C),
    onLate = Color(0xFF2A0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val ObsidianColors = ObsidianDarkColors

// -------------------------------------------------------------
// PRESET 4: AMBER (Warm Honey Amber & Golden Slate)
// -------------------------------------------------------------
val AmberLightColors = BirtuColors(
    bg = Color(0xFFFAF8F3),         // Soft golden off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces
    surfaceRaised = Color(0xFFFDF7EB),// Subtle amber raised container
    surfaceHigh = Color(0xFFF9EED9),// Soft honey container
    outline = Color(0xFFEADCC6),    // Soft amber outline
    text = Color(0xFF221A0C),       // Deep golden dark ink
    text2 = Color(0xFF706046),      // Muted golden gray text
    primary = Color(0xFFD97706),    // Warm Amber Gold
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFFEF3C7),// Soft amber chip tint
    primaryStrong = Color(0xFFB45309),
    late = Color(0xFFC62F3F),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x66221A0C),
    isDark = false
)

val AmberDarkColors = BirtuColors(
    bg = Color(0xFF0E0C0A),         // Deep golden charcoal
    surface = Color(0xFF171410),    // Muted dark amber card
    surfaceRaised = Color(0xFF201B16),// Slightly raised amber slate
    surfaceHigh = Color(0xFF2B251E),// Subtle amber container
    outline = Color(0xFF383027),    // Soft amber outline
    text = Color(0xFFFDFBF7),       // Crisp off-white
    text2 = Color(0xFFB8A990),      // Muted amber gray
    primary = Color(0xFFF59E0B),    // Amber gold accent
    onPrimary = Color(0xFF261500),
    primarySoft = Color(0xFF2D2314),// Subtle amber tint
    primaryStrong = Color(0xFFFBBF24),
    late = Color(0xFFF43F5E),
    onLate = Color(0xFF2B0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val AmberColors = AmberLightColors

// -------------------------------------------------------------
// PRESET 5: EMERALD (Emerald Green & Forest Mint)
// -------------------------------------------------------------
val EmeraldLightColors = BirtuColors(
    bg = Color(0xFFF4FAF6),         // Soft forest off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces
    surfaceRaised = Color(0xFFEEF8F2),// Subtle mint raised container
    surfaceHigh = Color(0xFFDCF0E5),// Soft mint container
    outline = Color(0xFFCBE3D5),    // Soft mint outline
    text = Color(0xFF0B2117),       // Deep emerald ink text
    text2 = Color(0xFF486C5A),      // Muted forest gray text
    primary = Color(0xFF059669),    // Emerald Mint accent
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFD1FAE5),// Soft mint chip tint
    primaryStrong = Color(0xFF047857),
    late = Color(0xFFE11D48),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x660B2117),
    isDark = false
)

val EmeraldDarkColors = BirtuColors(
    bg = Color(0xFF090E0C),         // Deep forest charcoal
    surface = Color(0xFF121815),    // Muted dark forest card
    surfaceRaised = Color(0xFF1A211D),// Slightly raised forest slate
    surfaceHigh = Color(0xFF232E28),// Subtle forest container
    outline = Color(0xFF2C3C34),    // Soft forest outline
    text = Color(0xFFECFDF5),       // Crisp off-white
    text2 = Color(0xFF86A395),      // Muted mint gray
    primary = Color(0xFF10B981),    // Emerald mint accent
    onPrimary = Color(0xFF022018),
    primarySoft = Color(0xFF152921),// Subtle mint tint
    primaryStrong = Color(0xFF34D399),
    late = Color(0xFFF43F5E),
    onLate = Color(0xFF2B0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val EmeraldColors = EmeraldLightColors

// -------------------------------------------------------------
// PRESET 6: BUBBLEGUM (Berry Pink & Cotton Candy)
// -------------------------------------------------------------
val BubblegumLightColors = BirtuColors(
    bg = Color(0xFFFAF4F6),         // Soft rose off-white canvas
    surface = Color(0xFFFFFFFF),    // Clean white surfaces
    surfaceRaised = Color(0xFFFDF0F4),// Subtle pink raised container
    surfaceHigh = Color(0xFFFCE1EB),// Soft pink container
    outline = Color(0xFFF3D2DF),    // Soft pink outline
    text = Color(0xFF260D17),       // Deep berry ink text
    text2 = Color(0xFF734B59),      // Muted rose gray text
    primary = Color(0xFFE11D48),    // Berry Rose accent
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFFFE4E6),// Soft rose chip tint
    primaryStrong = Color(0xFFBE123C),
    late = Color(0xFFBE123C),
    onLate = Color(0xFFFFFFFF),
    scrim = Color(0x66260D17),
    isDark = false
)

val BubblegumDarkColors = BirtuColors(
    bg = Color(0xFF0F0B0E),         // Deep berry charcoal
    surface = Color(0xFF181216),    // Muted dark berry card
    surfaceRaised = Color(0xFF21191E),// Slightly raised berry slate
    surfaceHigh = Color(0xFF2B2127),// Subtle berry container
    outline = Color(0xFF3B2B35),    // Soft berry outline
    text = Color(0xFFFFF1F5),       // Crisp off-white
    text2 = Color(0xFFA88F98),      // Muted rose gray
    primary = Color(0xFFF43F5E),    // Berry pink accent
    onPrimary = Color(0xFFFFFFFF),
    primarySoft = Color(0xFF2E1922),// Subtle rose tint
    primaryStrong = Color(0xFFFF6289),
    late = Color(0xFFF43F5E),
    onLate = Color(0xFF2B0A10),
    scrim = Color(0x99000000),
    isDark = true
)

val BubblegumColors = BubblegumLightColors

val LocalBirtuColors = staticCompositionLocalOf { TangeloColors }
