---
name: Aethera
colors:
  surface: '#fdf8f8'
  surface-dim: '#ddd9d9'
  surface-bright: '#fdf8f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f7f3f2'
  surface-container: '#f1edec'
  surface-container-high: '#ebe7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#46464a'
  inverse-surface: '#313030'
  inverse-on-surface: '#f4f0ef'
  outline: '#77767b'
  outline-variant: '#c7c6ca'
  surface-tint: '#5f5e60'
  primary: '#010102'
  on-primary: '#ffffff'
  primary-container: '#1c1c1e'
  on-primary-container: '#858486'
  inverse-primary: '#c8c6c8'
  secondary: '#605e5a'
  on-secondary: '#ffffff'
  secondary-container: '#e6e2dd'
  on-secondary-container: '#666460'
  tertiary: '#010100'
  on-tertiary: '#ffffff'
  tertiary-container: '#1f1b1a'
  on-tertiary-container: '#8a8381'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e4e2e4'
  primary-fixed-dim: '#c8c6c8'
  on-primary-fixed: '#1b1b1d'
  on-primary-fixed-variant: '#474649'
  secondary-fixed: '#e6e2dd'
  secondary-fixed-dim: '#cac6c1'
  on-secondary-fixed: '#1d1b19'
  on-secondary-fixed-variant: '#484643'
  tertiary-fixed: '#eae0de'
  tertiary-fixed-dim: '#cdc5c3'
  on-tertiary-fixed: '#1f1b1a'
  on-tertiary-fixed-variant: '#4b4644'
  background: '#fdf8f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '600'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-md:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 12px
  md: 16px
  lg: 24px
  xl: 32px
  margin-mobile: 20px
  gutter: 12px
---

## Brand & Style
The design system is rooted in **Modern Minimalism**, emphasizing a gender-neutral, premium aesthetic that prioritizes clarity and spatial harmony. It draws inspiration from high-end editorial layouts, using generous whitespace and a restricted color palette to allow product imagery to remain the focal point.

The emotional response is one of "Quiet Luxury"—sophisticated, intentional, and effortless. By stripping away non-essential decorative elements and focusing on precise alignment and typographic hierarchy, the UI achieves a curated feel similar to global boutique brands.

## Colors
This design system utilizes a high-contrast, neutral-heavy palette to maintain a premium and gender-neutral tone. 

- **Deep Charcoal (#1C1C1E)** serves as the primary driver for high-action elements and text, ensuring grounding and authority.
- **Warm Ivory (#F5F0EB)** is used for subtle container backgrounds or secondary sections to soften the clinical feel of pure white.
- **Amber Gold (#E8A020)** is reserved strictly for tactical accents: price highlights, notification dots, or "Add to Cart" confirmation states.
- **Surface and Background** colors differentiate between the app canvas (#F8F8F8) and elevated interactive cards (#FFFFFF).

## Typography
The system utilizes **Inter** exclusively to leverage its systematic, utilitarian nature. The type scale is designed with a strong vertical rhythm.

- **Headlines:** Use Semi-Bold weights with tighter letter-spacing to create a "block" feel for section titles.
- **Body:** Standardized at 16px for readability, maintaining a 1.5x line-height to ensure the interface feels "airy."
- **Labels:** Small labels for categories or utility text use All-Caps with increased letter-spacing (0.05em) to differentiate them from body content without increasing visual weight.

## Layout & Spacing
This design system follows a **8pt grid system** with a 4pt baseline for micro-adjustments. 

The layout utilizes a **fluid grid** for mobile, featuring a generous 20px outer margin to prevent the UI from feeling cramped. Content is organized in a vertical stack with significant 32px or 48px gaps between major sections (e.g., "New Arrivals" vs "Editor's Pick") to reinforce the minimal, editorial aesthetic. Internal card padding is standardized at 16px.

## Elevation & Depth
In line with the premium, clean style, this design system avoids heavy drop shadows. Instead, it utilizes **Ambient Shadows** and **Tonal Layering**:

- **Level 0 (Background):** #F8F8F8.
- **Level 1 (Cards/Surface):** #FFFFFF with a very soft, diffused shadow (0px 4px 20px, 4% opacity of #1C1C1E).
- **Level 2 (Modals/Popups):** #FFFFFF with a slightly more pronounced shadow (0px 8px 30px, 8% opacity of #1C1C1E).

Depth is primarily communicated through the contrast between the Warm Ivory containers and the pure White background of product cards.

## Shapes
The shape language is defined by **Medium Roundedness**. 

- **Standard Elements:** Buttons, input fields, and small cards use a 12dp radius.
- **Large Containers:** Product image containers and bottom sheets use a 16dp radius.
- **Icons:** Use a 2px stroke weight with slightly rounded caps to match the UI's softness without appearing "bubbly."

The consistency of these radii across all components creates a cohesive, modern feel that balances the sharpness of the charcoal color palette.

## Components
- **Buttons:** Primary buttons are Deep Charcoal (#1C1C1E) with white text, no border. Secondary buttons use a transparent background with a 1px Charcoal border. Height is fixed at 56dp for high tap-target confidence.
- **Input Fields:** Outlined style with a subtle #E0E0E0 border that transitions to Deep Charcoal on focus. 12dp corner radius.
- **Chips:** Warm Ivory (#F5F0EB) backgrounds with Deep Charcoal text for unselected states. Selected states use Charcoal backgrounds.
- **Product Cards:** Minimalist "Frame-less" look. The image occupies the top portion with a 12dp radius, and text is left-aligned below with no containing border, using whitespace to separate items.
- **Navigation:** Material 3 Navigation Bar with a white surface and charcoal icons. The active state indicator should be a subtle Amber Gold dot rather than a large pill shape to maintain the minimal look.
- **Bottom Sheets:** Use 16dp top-corner rounding and a visible handle. They should transition smoothly from the bottom, appearing as an overlay over a dimmed background.