# 🎨 Aethera — UI Guidelines & Frontend Standards

> **Design System:** Stitch Indigo
> **UI Framework:** Jetpack Compose + Material 3
> **Purpose:** This document defines the visual language, component conventions, and Compose coding standards for the Aethera app.

---

## 1. Design System — Stitch Indigo

Aethera uses the **Stitch Indigo** design identity — a premium, high-contrast, and intuitive interface built on Material 3 (M3).

### Design Principles
| Principle | Description |
|-----------|-------------|
| **Premium** | Clean whites, rich indigo accents, purposeful use of negative space |
| **High Contrast** | Dark text on light surfaces; white icons/text on colored surfaces |
| **Purposeful Motion** | Smooth state transitions, loading indicators, no jarring snaps |
| **Consistency** | Every screen uses the same card shape, padding rhythm, and type scale |

---

## 2. Color Palette

Defined in the `theme/` package using M3 `ColorScheme`.

| Token | Role | Usage |
|-------|------|-------|
| `primary` | Indigo accent | Buttons, FABs, active icons, badges |
| `onPrimary` | White | Text/icons on primary-colored surfaces |
| `background` | Light grey/white | Screen background |
| `surface` | White | Cards, bottom sheets, dialogs |
| `onSurface` | Dark grey/black | Primary body text |
| `onSurfaceVariant` | Medium grey | Secondary text, placeholder icons |
| `error` | Red | Destructive actions (e.g., Sign Out button) |
| `onError` | White | Text on error surfaces |
| `surfaceContainer` | Subtle grey | TopAppBar container |

> **Rule:** Never use hardcoded `Color(0xFF...)` values in composables. Always reference `MaterialTheme.colorScheme.*` tokens.
> **Exception:** `Color.White` and `Color.DarkGray` are allowed in legacy components while migration is in progress.

---

## 3. Typography

Uses Material 3 type scale via `MaterialTheme.typography.*`.

| Style | Usage |
|-------|-------|
| `titleLarge` | TopAppBar app name ("Aethera") |
| `headlineMedium` | User name on Profile screen |
| `titleMedium` | Section headers, product names |
| `bodyLarge` | User email, product descriptions |
| `bodyMedium` | Supporting body text |
| `labelLarge` | Button labels |
| `labelSmall` | Badges, tags, captions |

### Font Weight Conventions
| Weight | Context |
|--------|---------|
| `FontWeight.Bold` | App title, key values (price, stats) |
| `FontWeight.SemiBold` | User names, section headers |
| `FontWeight.Normal` | General body text |

---

## 4. Shape & Spacing

### Shape Tokens (from M3 `Shapes`)
| Token | Usage |
|-------|-------|
| `MaterialTheme.shapes.small` | Chips, small tags |
| `MaterialTheme.shapes.medium` | Buttons, input fields |
| `MaterialTheme.shapes.large` | Cards, bottom sheets |
| `RoundedCornerShape(8.dp)` | InfoBox, MenuColumnBox (legacy) |

### Spacing Rhythm
| Value | Usage |
|-------|-------|
| `4.dp` | Icon internal padding |
| `8.dp` | Between elements in a Row/Column |
| `12.dp` | Card internal padding (small), section separators |
| `16.dp` | Standard horizontal screen padding |
| `20.dp` | LazyColumn horizontal content padding (Profile) |
| `24.dp` | Large section gaps |

---

## 5. Component Conventions

### 5.1 Screen Structure Pattern
Every screen follows the **Parent / Content** split:

```kotlin
// Parent — collects state, wires events
@Composable
fun FeatureScreen(
    innerPadding : PaddingValues,
    onNavigate   : () -> Unit,
    viewModel    : FeatureViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    FeatureContent(state = uiState, innerPadding = innerPadding, onNavigate = onNavigate)
}

// Content — pure UI, easily previewable
@Composable
fun FeatureContent(
    state        : FeatureUiState,
    innerPadding : PaddingValues,
    onNavigate   : () -> Unit
) { ... }
```

> **Why:** Keeps Composables previewable and free of DI dependencies.

---

### 5.2 TopAppBar
All screens use `TopAppBar` from M3. Standard pattern:

```kotlin
TopAppBar(
    title = { Text("Screen Title", style = MaterialTheme.typography.titleLarge) },
    navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

> **Rule:** Screens with bottom navigation (Home, Search, Cart, Profile) do **not** have a back arrow.

---

### 5.3 Bottom Navigation Bar
- Visible only on: `Home`, `Search`, `Cart`, `Profile`
- Uses M3 `NavigationBar` + `NavigationBarItem`
- Icons: outlined when unselected, filled when selected

---

### 5.4 Loading State
Use `CircularProgressIndicator` centered on the screen while data is loading:

```kotlin
if (state.isLoading) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
    return@Scaffold
}
```

---

### 5.5 InfoBox (Stats Card)
Used on Profile screen for Orders / Saves / Points stats.

```
┌──────────────────┐
│     ORDERS       │  ← labelSmall / SemiBold / DarkGray
│                  │
│       12         │  ← 24sp / Bold / Black
└──────────────────┘
  height: 90.dp, background: White, shape: RoundedCornerShape(8.dp)
```

---

### 5.6 MenuColumnBox (Navigation Row)
Used on Profile screen for menu items.

```
┌──────────────────────────────────────────────┐
│  [Icon]    My Orders                    [›]  │
└──────────────────────────────────────────────┘
  height: 60.dp, background: White, shape: RoundedCornerShape(8.dp)
  Leading icon: Outlined variant | Trailing: KeyboardArrowRight
```

---

### 5.7 Buttons
| Type | Usage | Color |
|------|-------|-------|
| `Button` | Primary actions (Place Order, Save) | `primary` |
| `Button` (error) | Destructive (Sign Out) | `error` |
| `TextButton` | Secondary/inline actions | transparent bg |
| `OutlinedButton` | Cancel, secondary CTA | transparent + border |

**Standard button sizing:**
```kotlin
Modifier.fillMaxWidth().height(52.dp)
shape = MaterialTheme.shapes.medium
```

---

## 6. Image Loading

Use **Coil 3** for all network images:

```kotlin
AsyncImage(
    model = imageUrl,
    contentDescription = "Product image",
    modifier = Modifier.fillMaxWidth(),
    contentScale = ContentScale.Crop
)
```

- Always provide a `contentDescription` for accessibility
- Use `placeholder` and `error` drawables where applicable

---

## 7. Icon Conventions

| Situation | Icon Style |
|-----------|-----------|
| Active / selected state | `Icons.Filled.*` |
| Default / unselected state | `Icons.Outlined.*` |
| Navigation arrows | `Icons.AutoMirrored.Filled.ArrowBack / KeyboardArrowRight` |

> **Source:** All icons from `androidx.compose.material.icons` — do not import external icon packs unless agreed upon.

---

## 8. Accessibility Standards

| Rule | Detail |
|------|--------|
| Content descriptions | All `Icon` composables must have a non-null `contentDescription` or explicit `null` with justification |
| Touch targets | Minimum 48×48.dp for all clickable elements |
| Color contrast | Text must meet WCAG AA contrast ratio (4.5:1 minimum) |
| Loading states | Always show `CircularProgressIndicator` instead of blank screens |

---

## 9. Compose Coding Standards

| Rule | Guideline |
|------|-----------|
| **Naming** | Screen composables: `PascalCase`. Private helpers: `PascalCase`. Parameters: `camelCase`. |
| **State hoisting** | Never hold state inside leaf composables. Hoist to ViewModel or parent Screen. |
| **Side effects** | Use `LaunchedEffect` for one-time events (e.g., logout navigation). |
| **Previews** | All `Content` composables should have a `@Preview` function. |
| **Modifier** | First parameter of every composable should accept `modifier: Modifier = Modifier`. |
| **No hardcoded strings** | UI strings should be moved to `strings.xml` (future improvement). |

---

## 10. Screen-Specific Notes

| Screen | Notes |
|--------|-------|
| `HomeScreen` | Category chips scroll horizontally; product grid is 2-column LazyVerticalGrid |
| `ProductDetailScreen` | Full-bleed image at top; Add to Cart + Wishlist actions at bottom |
| `CartScreen` | Swipe-to-delete on cart items; total price pinned at bottom |
| `ProfileScreen` | `LazyColumn` layout; stats row uses equal `weight(1f)` distribution |
| `CheckoutScreen` | Address + payment summary before order confirmation |
| `OrderHistoryScreen` | Sorted by `createdAt` desc; status chip color reflects order state |

---

*Last updated: May 2026 | Developed by Tarun Sharma*
