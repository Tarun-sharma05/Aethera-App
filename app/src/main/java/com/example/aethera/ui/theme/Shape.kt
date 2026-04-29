package com.example.aethera.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  Aethera Design System — Shapes
//  Source: Stitch DESIGN.md & screen components
//
//  Stitch token  → dp value  → M3 slot
//  sm (0.25rem)  → 4dp       → extraSmall
//  DEFAULT       → 8dp       → small
//  md (0.75rem)  → 12dp      → medium   ← buttons, inputs, small cards
//  lg (1rem)     → 16dp      → large    ← product image containers, bottom sheets
//  xl (1.5rem)   → 24dp      → extraLarge
//  full (9999px) → 50dp+     → (use CircleShape / RoundedCornerShape(50%))
// ─────────────────────────────────────────────

val AetheraShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Stitch: sm = 0.25rem
    small = RoundedCornerShape(8.dp),        // Stitch: DEFAULT = 0.5rem
    medium = RoundedCornerShape(12.dp),      // Stitch: md = 0.75rem — buttons, inputs, chips
    large = RoundedCornerShape(16.dp),       // Stitch: lg = 1rem — product cards, bottom sheets
    extraLarge = RoundedCornerShape(24.dp)   // Stitch: xl = 1.5rem
)
