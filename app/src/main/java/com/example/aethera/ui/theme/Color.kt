package com.example.aethera.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
//  Aethera Design System — Color Tokens
//  Source: Stitch DESIGN.md + screen HTML files
// ─────────────────────────────────────────────

// Primary
val Primary = Color(0xFF010102)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF1C1C1E)
val OnPrimaryContainer = Color(0xFF858486)
val InversePrimary = Color(0xFFC8C6C8)
val PrimaryFixed = Color(0xFFE4E2E4)
val PrimaryFixedDim = Color(0xFFC8C6C8)
val OnPrimaryFixed = Color(0xFF1B1B1D)
val OnPrimaryFixedVariant = Color(0xFF474649)

// Secondary
val Secondary = Color(0xFF605E5A)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE6E2DD)
val OnSecondaryContainer = Color(0xFF666460)
val SecondaryFixed = Color(0xFFE6E2DD)
val SecondaryFixedDim = Color(0xFFCAC6C1)
val OnSecondaryFixed = Color(0xFF1D1B19)
val OnSecondaryFixedVariant = Color(0xFF484643)

// Tertiary
val Tertiary = Color(0xFF010100)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFF1F1B1A)
val OnTertiaryContainer = Color(0xFF8A8381)
val TertiaryFixed = Color(0xFFEAE0DE)
val TertiaryFixedDim = Color(0xFFCDC5C3)
val OnTertiaryFixed = Color(0xFF1F1B1A)
val OnTertiaryFixedVariant = Color(0xFF4B4644)

// Error
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

// Surface
val Surface = Color(0xFFFDF8F8)
val SurfaceDim = Color(0xFFDDD9D9)
val SurfaceBright = Color(0xFFFDF8F8)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF7F3F2)
val SurfaceContainer = Color(0xFFF1EDEC)
val SurfaceContainerHigh = Color(0xFFEBE7E7)
val SurfaceContainerHighest = Color(0xFFE5E2E1)
val SurfaceVariant = Color(0xFFE5E2E1)
val SurfaceTint = Color(0xFF5F5E60)
val OnSurface = Color(0xFF1C1B1B)
val OnSurfaceVariant = Color(0xFF46464A)
val InverseSurface = Color(0xFF313030)
val InverseOnSurface = Color(0xFFF4F0EF)

// Background
val Background = Color(0xFFFDF8F8)
val OnBackground = Color(0xFF1C1B1B)

// Outline
val Outline = Color(0xFF77767B)
val OutlineVariant = Color(0xFFC7C6CA)

// Accent (Amber Gold — tactical accent from Stitch design notes)
val AmberGold = Color(0xFFE8A020)

// ─────────────────────────────────────────────
//  Dark Theme Derivations
//  Derived from Material 3 tonal palette inversion
//  principles, using Stitch inverse-* tokens where
//  provided and sensible dark equivalents otherwise.
// ─────────────────────────────────────────────

// Dark — Primary
val PrimaryDark = Color(0xFFC8C6C8)          // inverse-primary
val OnPrimaryDark = Color(0xFF313030)        // inverse-surface as container
val PrimaryContainerDark = Color(0xFF474649)
val OnPrimaryContainerDark = Color(0xFFE4E2E4)

// Dark — Secondary
val SecondaryDark = Color(0xFFCAC6C1)        // secondary-fixed-dim
val OnSecondaryDark = Color(0xFF1D1B19)
val SecondaryContainerDark = Color(0xFF484643)
val OnSecondaryContainerDark = Color(0xFFE6E2DD)

// Dark — Tertiary
val TertiaryDark = Color(0xFFCDC5C3)         // tertiary-fixed-dim
val OnTertiaryDark = Color(0xFF1F1B1A)
val TertiaryContainerDark = Color(0xFF4B4644)
val OnTertiaryContainerDark = Color(0xFFEAE0DE)

// Dark — Error
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// Dark — Surface
val SurfaceDark = Color(0xFF131212)
val SurfaceDimDark = Color(0xFF131212)
val SurfaceBrightDark = Color(0xFF393737)
val SurfaceContainerLowestDark = Color(0xFF0E0D0D)
val SurfaceContainerLowDark = Color(0xFF1C1B1B)
val SurfaceContainerDark = Color(0xFF201F1F)
val SurfaceContainerHighDark = Color(0xFF2B2A2A)
val SurfaceContainerHighestDark = Color(0xFF363434)
val OnSurfaceDark = Color(0xFFE5E2E1)         // inverse-on-surface light variant
val OnSurfaceVariantDark = Color(0xFFC7C6CA)  // outline-variant light
val InverseSurfaceDark = Color(0xFFE5E2E1)
val InverseOnSurfaceDark = Color(0xFF313030)

// Dark — Background
val BackgroundDark = Color(0xFF131212)
val OnBackgroundDark = Color(0xFFE5E2E1)

// Dark — Outline
val OutlineDark = Color(0xFF928F94)
val OutlineVariantDark = Color(0xFF46464A)