# Design System Strategy: The Gentle Guardian

This design system is a bespoke framework crafted for a Japanese family-centric financial experience. We are moving away from the cold, rigid structures of "Traditional Fintech" and toward an "Editorial Sanctuary" for savings. The goal is to balance the absolute security of a bank with the warmth of a digital scrapbook.

---

## 1. Overview & Creative North Star

**Creative North Star: "The Digital Heirloom"**
Traditional Japanese financial apps often suffer from "Information Density Fatigue." This design system counters that by treating every screen like a high-end editorial layout. We break the "template" look through **Intentional Asymmetry**—using generous, purposeful white space to guide the eye toward growth (savings) and safety. 

By overlapping soft-edged containers and using high-contrast typography scales, we create a UI that feels curated rather than programmed. The experience should feel like a "Safe Harbor": calm, spacious, and premium.

---

## 2. Color Strategy & The "No-Line" Rule

Our palette leverages the psychology of Japanese "Shinrai" (Trust). We use deep blues for authority, tempered by secondary mints and tertiary oranges that evoke the warmth of a family home.

### The "No-Line" Rule
To achieve a premium, modern feel, **1px solid borders for sectioning are strictly prohibited.** 
*   **The Law:** Boundaries must be defined solely through background color shifts. 
*   **The Execution:** Place a `surface-container-low` section against a `surface` background. If you need to separate content within a card, use a `3` (1rem) spacing gap or a tonal shift rather than a divider line.

### Surface Hierarchy & Nesting
Think of the UI as physical layers of frosted glass.
*   **Level 0 (Base):** `surface` (#f8f9ff) - The canvas.
*   **Level 1 (Sections):** `surface-container-low` (#eff3ff) - Used for grouping broad content areas.
*   **Level 2 (Cards):** `surface-container-lowest` (#ffffff) - Used for the most important interactive elements, creating a "lift" from the blue-tinted base.

### Glass & Signature Textures
*   **Glassmorphism:** For floating navigation or modal overlays, use `surface-container-lowest` at 80% opacity with a `backdrop-filter: blur(20px)`.
*   **Gradients:** Use a subtle linear gradient from `primary` (#0060ad) to `primary-container` (#9ac3ff) on hero "Goal" cards. This provides a "soulful" depth that flat colors lack.

---

## 3. Typography: Editorial Authority

We use a dual-font approach to create a sophisticated hierarchy. **Plus Jakarta Sans** provides a modern, geometric feel for numbers and English headers, while **Manrope** (paired with Noto Sans JP for Japanese characters) ensures maximum legibility for the family.

*   **Display (Investment in Growth):** Use `display-lg` for total savings amounts. The large scale conveys importance without needing "bold" weights.
*   **Headlines (The Narrative):** `headline-md` should be used for section titles (e.g., "Future Education Fund"). 
*   **Body (The Guidance):** `body-md` is our workhorse. In the Japanese context, ensure a line-height of at least 1.6 to prevent the character-dense text from feeling "corporate" or cramped.

---

## 4. Elevation & Depth: Tonal Layering

We convey hierarchy through light and color, not structure.

*   **The Layering Principle:** Place a `surface-container-lowest` card on top of a `surface-container-high` background to create a natural, soft lift.
*   **Ambient Shadows:** For "Floating Action Buttons" or critical modals, use a shadow with a 24px blur, 10% opacity, using the `on-surface` color (#173355) rather than pure black. This mimics natural light.
*   **Ghost Border Fallback:** If a border is required for accessibility, use `outline-variant` (#99b4dc) at **15% opacity**. It should be felt, not seen.

---

## 5. Signature Components

### Primary Buttons
*   **Style:** `primary` (#0060ad) background with `on-primary` (#f8f8ff) text.
*   **Rounding:** Always `full` (pill-shaped) to maximize "approachability."
*   **Interaction:** On hover, shift to `primary-dim` (#005498). Avoid harsh color changes.

### Savings Progress Cards
*   **Layout:** Asymmetric. The current balance is `display-sm` top-left, while the "Goal" is `label-md` bottom-right.
*   **Background:** Use a `secondary-container` (#b1fde6) for "Positive Growth" states to signal health and safety.

### Input Fields
*   **Visuals:** No bottom line. Use a `surface-container-highest` (#d4e3ff) background with `xl` (1.5rem) rounded corners.
*   **Focus:** Transition the background to `surface-container-lowest` and apply a 2px "Ghost Border" using `primary`.

### Transaction Lists
*   **Restriction:** **Forbid all divider lines.**
*   **Structure:** Each transaction is a group with `3` (1rem) vertical spacing. Use a tiny `secondary` dot to indicate "Savings Added" and an `error` dot for "Withdrawals."

### Achievement Chips
*   **Context:** For when a child hits a savings milestone.
*   **Style:** `tertiary-container` (#feb246) background with `on-tertiary-container` (#563500) text. These should feel like "warm sunshine."

---

## 6. Do’s and Don’ts

### Do
*   **Do use "Ma" (Negative Space):** If a screen feels busy, increase spacing from `4` to `6`. In Japanese design, space is a sign of premium quality.
*   **Do prioritize Tonal Contrast:** Ensure that `on-surface` text on `surface-container` backgrounds meets WCAG AA standards.
*   **Do use Soft Iconography:** Use rounded, "duotone" icons where the secondary color is at 30% opacity.

### Don’t
*   **Don't use pure black (#000000):** It is too aggressive for a family app. Use `on-surface` (#173355) for all high-contrast text.
*   **Don't use "Hard" corners:** Nothing in this app should be sharper than the `DEFAULT` (0.5rem) radius.
*   **Don't use standard Material dividers:** Lines create "cages" for data. Let the data breathe through background shifts.