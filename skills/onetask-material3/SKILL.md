---
name: onetask-material3
description: >
  Material 3 design system guidelines for the OneTask project (Compose Multiplatform, Desktop JVM).
  Read this skill before creating or modifying any UI component — covers color usage,
  typography scale, spacing system, shape tokens, component patterns, and adaptive layout rules.
---

# OneTask — Material 3 Design Guidelines

## Stack Context

- **Framework**: Compose Multiplatform 1.11.x (JVM Desktop target)
- **Design system**: Material 3 (`androidx.compose.material3`)
- **Custom theme**: `OneTaskTheme` in `Theme.kt` — always wrap screens in this; never call `MaterialTheme {}` directly.
- **Font family**: `SansFontFamily()` — loaded from `composeResources/font/`. Use `MaterialTheme.typography` scale; never set `fontFamily` ad-hoc.

---

## Color System — Use Token Names, Never Raw Colors

Always use `MaterialTheme.colorScheme.<token>`. **Never** hardcode `Color(0xFF...)` in components.
`Theme.kt` already maps brand colors to M3 tokens for both dark and light themes.

### Color Token Cheat Sheet

| Token | Dark value | Light value | Correct use |
|---|---|---|---|
| `primary` | `#D0BCFF` (lavender) | `#6750A4` (purple) | Active/selected state, CTAs, accent |
| `primaryContainer` | `#A078FF` | `#EADDFF` | Filled icon chip backgrounds, app logo bg |
| `onPrimaryContainer` | `#340080` | `#21005D` | Text/icon ON `primaryContainer` |
| `secondary` | `#ADC6FF` (blue) | `#625B71` | Secondary actions, metadata |
| `tertiary` | `#FFB869` (amber) | `#7D5260` | Warnings, highlights, special badges |
| `error` | `#FFB4AB` | `#B3261E` | Error states, destructive actions |
| `errorContainer` | `#93000A` | `#F9DEDC` | Error chip / snackbar background |
| `background` | `#131315` | `#FFFBFE` | Root surface behind everything |
| `surface` | `#131315` | `#FFFBFE` | Cards, dialogs, sheets at elevation 0 |
| `onSurface` | `#E5E1E4` | `#1C1B1F` | Primary content text on surface |
| `onSurfaceVariant` | `#CBC3D7` | `#49454F` | Secondary text, inactive icons |
| `surfaceContainerLowest` | `#0E0E10` | `#FFFFFF` | Rail sidebar (darkest panel) |
| `surfaceContainerLow` | `#1B1B1D` | `#F7F2F9` | Pages sidebar |
| `surfaceContainer` | `#201F21` | `#F3EDF7` | Category headers, input fields |
| `surfaceContainerHigh` | `#2A2A2C` | `#ECE6F0` | Hover states, selected rows, chips |
| `surfaceContainerHighest` | `#353437` | `#E6E0E9` | Tooltips, popovers |
| `outline` | `#958EA0` | `#79747E` | Borders, dividers (full opacity) |
| `outlineVariant` | `#494454` | `#CAC4D0` | Subtle dividers (use `.copy(alpha=0.3f)`) |

### Color Usage Rules

```kotlin
// ✅ Correct — use token
Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh))

// ❌ Wrong — hardcoded hex
Text(text, color = Color(0xFFCBC3D7))
Box(modifier = Modifier.background(Color.DarkGray))

// ✅ Dimming / alpha adjustments are allowed
MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) // For hover tints
```

---

## Typography Scale — Use `MaterialTheme.typography`

| Role | Token | Use case |
|---|---|---|
| Display | `displayLarge/Medium/Small` | Hero text, splash |
| Headline | `headlineLarge/Medium/Small` | Screen section titles |
| Title | `titleLarge/Medium/Small` | Card titles, dialog headers, sidebar section headings |
| Body | `bodyLarge/Medium/Small` | Main readable content, block text |
| Label | `labelLarge/Medium/Small` | Chips, buttons, breadcrumbs, metadata |

```kotlin
// ✅ Correct
Text("Pages", style = MaterialTheme.typography.headlineMedium)
Text("Last edited 2m ago", style = MaterialTheme.typography.labelMedium)
Text(block.text, style = MaterialTheme.typography.bodyLarge)

// ✅ Allowed — customize within the token style
Text(
    "SECTION",
    style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
)

// ❌ Wrong — ad-hoc font sizes
Text("Hello", fontSize = 14.sp, fontFamily = SansFontFamily())
```

---

## Spacing System

Use **multiples of 4.dp** for all padding and spacing. Avoid arbitrary values.

| Size | Value | When to use |
|---|---|---|
| `xs` | `4.dp` | Icon padding, tight insets |
| `sm` | `8.dp` | Component internal padding, gaps between small items |
| `md` | `12.dp` | Standard gap between list items |
| `lg` | `16.dp` | Section padding, card internal padding |
| `xl` | `24.dp` | Screen edge padding on compact layouts |
| `xxl` | `32.dp` | Screen edge padding on expanded layouts (TopAppBar uses `32.dp`) |

```kotlin
// ✅ Correct
Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
Spacer(modifier = Modifier.height(16.dp))
Arrangement.spacedBy(8.dp)

// ❌ Wrong
Modifier.padding(15.dp) // not a multiple of 4
Spacer(modifier = Modifier.height(10.dp))
```

---

## Shape Tokens

| Shape | Radius | Use case |
|---|---|---|
| `ExtraSmall` | `4.dp` | Chips, breadcrumb badges |
| `Small` | `8.dp` | Buttons, input fields, list item rows |
| `Medium` | `12.dp` | Rail icon buttons, notebook icons |
| `Large` | `16.dp` | Cards, dialogs, bottom sheets |
| `ExtraLarge` | `28.dp` | FABs |
| `Full` | `CircleShape` | Avatar icons, circular FABs, avatar containers |

```kotlin
// ✅ Use RoundedCornerShape with these sizes
Modifier.clip(RoundedCornerShape(8.dp))   // Small — list row
Modifier.clip(RoundedCornerShape(12.dp))  // Medium — rail icon
Modifier.clip(RoundedCornerShape(16.dp))  // Large — dialog
Modifier.clip(CircleShape)                // Full — circular button
```

---

## Elevation / Surface Hierarchy

OneTask uses **surface container levels** (not elevation shadows) to create visual depth,
consistent with M3's "tonal surface" model.

Layer hierarchy from back to front:

```
background (root)
  └── surfaceContainerLowest  (Rail sidebar)
        └── surfaceContainerLow  (Pages sidebar)
              └── surfaceContainer  (Category headers, input row)
                    └── surfaceContainerHigh  (Hover / active rows, chips)
                          └── surfaceContainerHighest  (Tooltip, overlay)
```

**Rule**: Each panel/component should be one step "higher" than its parent container.

---

## Standard Component Patterns

### List Row (sidebar page item)
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(
            if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh
            else Color.Transparent
        )
        .clickable { onSelect() }
        .padding(start = 24.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(icon, contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary
               else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(label,
        style = MaterialTheme.typography.titleMedium,
        color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
    )
}
```

### Icon Button (small, square)
```kotlin
Box(
    modifier = Modifier
        .size(32.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable { onClick() },
    contentAlignment = Alignment.Center
) {
    Icon(
        icon,
        contentDescription = stringResource(Res.string.content_desc_xxx),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(16.dp)
    )
}
```

### Section Header (category)
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        name.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
```

### Divider
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
)
```

### Input / Search Row
```kotlin
Row(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(Icons.Outlined.Search, null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
    )
    Spacer(Modifier.width(8.dp))
    // TextField or placeholder text here
}
```

---

## Adaptive Layout Rules

`WorkspaceScreen` already implements M3 adaptive breakpoints via `BoxWithConstraints`:

| Breakpoint | `maxWidth` | Layout |
|---|---|---|
| Compact | `< 600.dp` | No rail, no pages sidebar — single column |
| Medium | `600–840.dp` | Rail sidebar shown, pages sidebar hidden |
| Expanded | `≥ 840.dp` | Rail + Pages sidebar + Editor all visible |

**Rules when adding new layout:**
1. Always use `BoxWithConstraints` and check `maxWidth` to drive visibility.
2. Use `Modifier.weight(1f)` for the main content area — never hardcode widths for the main panel.
3. Panels have fixed widths: Rail = `64.dp`, Pages sidebar = `260.dp`.
4. Use `AnimatedVisibility` for panel show/hide transitions.

```kotlin
BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isCompact  = maxWidth < 600.dp
    val isExpanded = maxWidth >= 840.dp
    Row(modifier = Modifier.fillMaxSize()) {
        if (!isCompact)  { RailSidebar(…) }
        if (isExpanded)  { PagesSidebar(…) }
        MainContent(modifier = Modifier.weight(1f))
    }
}
```

---

## Hover / Interaction States (Desktop-specific)

Use `hoverableBackground` (already defined in `Hoverable.kt`) or `animateColorAsState` for hover:

```kotlin
// Preferred — use existing utility
Modifier.hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh)

// For icon rail buttons — animate border + background
val animatedBg by animateColorAsState(
    if (isHovered) iconColor.copy(alpha = 0.08f) else Color.Transparent
)
Modifier.background(animatedBg)
```

---

## Anti-Patterns — Never Do These

```kotlin
// ❌ Raw color values
color = Color(0xFF353437)

// ❌ Custom font size outside the type scale
fontSize = 13.sp

// ❌ Non-multiple-of-4 spacing
padding(top = 6.dp, bottom = 10.dp)

// ❌ Elevation-based shadows (use surface container levels instead)
Card(elevation = CardDefaults.cardElevation(8.dp))

// ❌ Hardcoded width for the main content pane
Column(modifier = Modifier.width(500.dp))

// ❌ Using surface instead of the correct container level
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) // only for root/dialog
```
