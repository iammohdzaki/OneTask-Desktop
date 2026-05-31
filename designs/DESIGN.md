---
name: Obsidian Deep
colors:
  surface: '#131315'
  surface-dim: '#131315'
  surface-bright: '#39393b'
  surface-container-lowest: '#0e0e10'
  surface-container-low: '#1b1b1d'
  surface-container: '#201f21'
  surface-container-high: '#2a2a2c'
  surface-container-highest: '#353437'
  on-surface: '#e5e1e4'
  on-surface-variant: '#cbc3d7'
  inverse-surface: '#e5e1e4'
  inverse-on-surface: '#303032'
  outline: '#958ea0'
  outline-variant: '#494454'
  surface-tint: '#d0bcff'
  primary: '#d0bcff'
  on-primary: '#3c0091'
  primary-container: '#a078ff'
  on-primary-container: '#340080'
  inverse-primary: '#6d3bd7'
  secondary: '#adc6ff'
  on-secondary: '#002e6a'
  secondary-container: '#0566d9'
  on-secondary-container: '#e6ecff'
  tertiary: '#ffb869'
  on-tertiary: '#482900'
  tertiary-container: '#ca801e'
  on-tertiary-container: '#3f2300'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e9ddff'
  primary-fixed-dim: '#d0bcff'
  on-primary-fixed: '#23005c'
  on-primary-fixed-variant: '#5516be'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#adc6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004395'
  tertiary-fixed: '#ffdcbb'
  tertiary-fixed-dim: '#ffb869'
  on-tertiary-fixed: '#2c1700'
  on-tertiary-fixed-variant: '#673d00'
  background: '#131315'
  on-background: '#e5e1e4'
  surface-variant: '#353437'
typography:
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  title-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.01em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 28px
    letterSpacing: 0em
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  rail-width: 64px
  sidebar-width: 260px
  gutter: 1px
  padding-page: 32px
  stack-gap: 8px
---

## Brand & Style
This design system focuses on high-productivity focus and information density, tailored for a desktop-first task and note management environment. The brand personality is professional, technical, and unobtrusive, designed to recede into the background so user content remains the focal point.

The design style is a refined **Modern Corporate** approach with a **Discord-inspired** multi-pane architecture. It utilizes Material 3's tonal elevation system to create a clear spatial mental model. By using varying levels of dark surfaces rather than heavy borders, the UI achieves a sophisticated, "pro-tool" aesthetic that reduces eye strain during long working sessions.

## Colors
The palette is built on a "Deep Charcoal" foundation to ensure maximum contrast for text while maintaining a comfortable dark-mode experience.

- **Surface Container Lowest:** Reserved for the far-left utility rail (workspace switcher, global settings).
- **Surface Container Low:** Utilized for the navigation/sidebar pane (folders, lists, tags).
- **Surface (Main):** The primary canvas for the editor and task details.
- **Primary:** An electric violet used sparingly for active states, primary actions, and focus indicators.
- **Outline Variant:** A low-contrast grey used for structural dividers and table borders to maintain order without visual clutter.

## Typography
The typographic scale emphasizes scannability and structural hierarchy. 

**Hanken Grotesk** provides a sharp, contemporary feel for headings and navigation, ensuring the UI feels precise. **Inter** is used for the body text to provide maximum legibility during long-form note-taking. **JetBrains Mono** is introduced for labels and utility tags to provide a distinct "data" look, useful for metadata like timestamps, file sizes, or technical tags.

## Layout & Spacing
The layout follows a **Fixed Multi-Pane** model optimized for wide desktop screens. 

1.  **Global Rail (64px):** Fixed to the left, containing workspace icons.
2.  **Navigation Pane (260px):** Collapsible sidebar for content hierarchy.
3.  **Editor Canvas:** Fluid center area that expands to fill remaining space.
4.  **Inspector (Optional):** Contextual right-aligned pane for metadata.

Spacing follows an 8px linear scale. Gutters between major panes should use the `outline_variant` color at 1px width to define boundaries without adding bulk. Content within the editor should maintain generous 32px horizontal margins to promote focus.

## Elevation & Depth
Depth is communicated through **Tonal Layers** rather than shadows. In this dark environment, light-source shadows are visually "heavy," so we rely on surface luminosity:

- **Level 0 (Lowest):** The background rail, appearing furthest away.
- **Level 1 (Low):** Navigation sidebar, slightly lighter.
- **Level 2 (Main):** The editor canvas, the "highest" and most luminous surface.
- **Level 3 (Overlay):** Modals and dropdown menus use a subtle `0px 4px 20px rgba(0,0,0,0.5)` shadow combined with a slightly lighter surface hex to appear floating.

## Shapes
Following Material 3 guidelines, the design system uses a logic of "Inner vs Outer" rounding. 

- **Containers:** Large cards and the editor canvas use `rounded-lg` (16px).
- **Components:** Buttons, input fields, and chips use `rounded` (8px).
- **Indicators:** Active state indicators on the far-left rail use "Pill" shapes (full radius) to contrast against the rectangular structure of the panes.

## Components
- **Buttons:** Primary buttons use a solid Electric Violet fill with white text. Secondary buttons use an `outline_variant` border with no fill.
- **Navigation Items:** Use a "Ghost" style. No background in default state; `surface_container_high` on hover; a vertical 4px "pill" on the left edge with a subtle primary-tinted background for the active state.
- **Input Fields:** Flat styling with a bottom-only border in `outline_variant`. On focus, the border transitions to Primary with a 2px thickness.
- **Chips/Tags:** Use `label-md` typography. Backgrounds are low-opacity versions of the primary color (10-15%) to remain legible without being distracting.
- **The Editor:** A "clean slate" approach. No visible borders around the text area; only the content and a subtle blinking Primary cursor.
- **Task List:** Interactive checkboxes use the Primary color when checked. Task rows highlight on hover using a subtle `surface_container_low` background.