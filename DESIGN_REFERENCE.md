# Visual Design Reference - Battery Alarm UI

## Color Specification

### Primary Colors
```
Background:    #0F1115  (RGB: 15, 17, 21)     - Very dark gray/black
Card:          #1C1F24  (RGB: 28, 31, 36)     - Dark gray
Accent:        #4CAF50  (RGB: 76, 175, 80)    - Green
Text Primary:  #FFFFFF  (RGB: 255, 255, 255)  - White
Text Secondary:#9AA0A6  (RGB: 154, 160, 166)  - Medium gray
Error:         #FF6B6B  (RGB: 255, 107, 107)  - Red
```

### Usage
- **Background**: Screen backgrounds (`DarkBackground`)
- **Card**: Card/Surface backgrounds (`CardBackground`)
- **Accent**: Buttons, highlights, active states (`BatteryGreen`)
- **Text Primary**: Main text, titles, headings
- **Text Secondary**: Subtitles, descriptions, hints (`SecondaryText`)
- **Error**: Delete actions, error states (`ErrorRed`)

---

## Typography Scale

### Heading 1 - Screen Title
- Size: 28sp
- Weight: Bold
- Color: White
- Used: "Battery Alarm" (MainScreen), "Create Alarm" (TopBar)

### Heading 2 - Section Title
- Size: 18sp
- Weight: SemiBold
- Color: White
- Used: "Battery Level", "Alert Type"

### Body Large - Card Title
- Size: 16sp
- Weight: SemiBold
- Color: White
- Used: Alarm type title in cards

### Body Medium - Subtitle
- Size: 14sp
- Weight: Regular
- Color: Secondary Gray
- Used: "Get alerted when your battery reaches..."

### Body Small - Card Subtitle
- Size: 13sp
- Weight: Regular
- Color: Secondary Gray
- Used: "Alarm when battery reaches 80%"

### Label Small - Helper Text
- Size: 12sp
- Weight: SemiBold
- Color: Green Accent
- Used: "2 Active" chip

---

## Component Specifications

### AlarmCard
```
Layout: Row
Padding: 16dp
Height: 64dp + padding
Background: #1C1F24
Border Radius: 18dp
Elevation: 0dp

Left Section (64dp):
├─ CircularProgressIndicator
│  ├─ Size: 64dp
│  ├─ Progress Color: #4CAF50
│  ├─ Track Color: White (6% opacity)
│  ├─ Stroke Width: 6dp
│  └─ Center Text: 14sp, Bold, White

Content Section (flex):
├─ Title: "Ring" or "Vibrate"
│  ├─ Size: 16sp
│  ├─ Weight: SemiBold
│  └─ Color: White
├─ Gap: 4dp
└─ Subtitle: "Alarm when battery reaches 80%"
   ├─ Size: 13sp
   └─ Color: #9AA0A6

Right Section:
├─ Switch (50dp x 24dp)
│  ├─ Checked: #4CAF50 (thumb), #4CAF50 50% (track)
│  └─ Unchecked: #9AA0A6 (thumb), #9AA0A6 30% (track)
├─ Gap: 8dp
└─ IconButton (36dp x 36dp)
   ├─ Icon: Delete (red #FF6B6B)
   └─ Rounded: 8dp
```

### PercentageChip
```
Layout: Surface
Size: Full Width x 50dp
Padding: 0dp
Content: Centered Text

Selected State:
├─ Background: #4CAF50
├─ Text Color: White
├─ Text Size: 14sp
├─ Weight: SemiBold
├─ Border Radius: 12dp
└─ Elevation: 4dp

Unselected State:
├─ Background: #1C1F24
├─ Text Color: #9AA0A6
├─ Text Size: 14sp
├─ Weight: SemiBold
├─ Border Radius: 12dp
└─ Elevation: 0dp
```

### AlertTypeCard
```
Layout: Card with Row content
Padding: 16dp
Background: #1C1F24 (unselected)
Border Radius: 14dp
Elevation: 0dp

Content Layout: Row with spacing 16dp
├─ Radio Button (24dp x 24dp)
│  ├─ Checked: #4CAF50
│  └─ Unchecked: #9AA0A6
├─ Icon (28dp x 28dp)
│  ├─ Ring: Icons.Notifications
│  ├─ Vibrate: Icons.Vibration
│  ├─ Color (checked): #4CAF50
│  └─ Color (unchecked): #9AA0A6
└─ Text Column (flex)
   ├─ Title: "Ring" or "Vibrate"
   │  ├─ Size: 16sp
   │  ├─ Weight: SemiBold
   │  ├─ Color: White
   │  └─ Bottom Gap: 2dp
   └─ Description: "Sound notification" or "Vibration only"
      ├─ Size: 13sp
      └─ Color: #9AA0A6

Selected State:
├─ Background: #4CAF50 (15% opacity)
├─ Border: 2dp #4CAF50
└─ Icons: Green (#4CAF50)

Unselected State:
├─ Background: #1C1F24
├─ Border: None
└─ Icons: Gray (#9AA0A6)
```

---

## Spacing & Layout

### Screen Level
- Top padding: 0dp (under TopBar)
- Horizontal padding: 24dp
- Section gap: 32dp
- Content padding: 16dp or 24dp

### Component Level
- Card padding: 16dp
- Card corner radius: 18dp (alarm), 14dp (alert), 12dp (chip)
- Component gaps: 8dp-12dp (inline), 12dp-16dp (vertical)

### Icons & Touch Targets
- Icon size: 24dp (radio), 28dp (alert), 32dp (battery)
- Button/IconButton: minimum 48dp x 48dp touch area
- Chip: 50dp height for tap-ability

---

## Density & Dimensions

### FAB (Floating Action Button)
```
Size: 56dp diameter
Margin: 16dp from edges
Background: #4CAF50
Icon: Add (24dp)
Icon Color: White
```

### TopAppBar
```
Height: 64dp (standard)
Background: #4CAF50
Title Text: 20sp, Bold, White
Icon Color: White
```

### Button (Create/Save)
```
Width: Full width
Height: 52dp
Background: #4CAF50
Corner Radius: 12dp
Text: "Create Alarm", 16sp, Bold, White
Padding: 0dp (content is sized to button)
```

### Active Chips Badge
```
Background: #4CAF50 (20% opacity)
Border Radius: 20dp
Padding: 12dp horizontal, 6dp vertical
Text: "X Active", 12sp, SemiBold, #4CAF50
```

---

## Animation & States

### Interactive States
- **Normal**: Default appearance
- **Pressed**: Button/Card slightly darker or with increased opacity
- **Disabled**: Grayed out appearance (if applicable)
- **Hover**: (Touch only, no hover on mobile)

### Transitions
- Switch animation: ~300ms
- Card selection: ~200ms color transition
- Button press: ~100ms feedback

---

## Dark Theme Properties

### Contrast Ratios
- White text on dark background: ~17:1 (AAA level)
- Green on dark background: ~3.5:1 (AA level for large text)
- Gray text on dark: ~5:1 (AA level)

### Accessibility
- All interactive elements: ≥48dp touch target
- Text size minimum: 12sp (body text)
- Color not only distinguishing feature
- Alt text on icons provided via contentDescription

---

## Responsive Design

### Breakpoints (Current: Optimized for phones)
- Minimum width: 320dp
- Standard width: 360-412dp
- Large width: 480dp+

### Adaptations
- Content padding increases on large screens
- Card width remains full-width (with padding)
- Chips may wrap to multiple rows on small screens
- Font sizes remain consistent

---

## Material3 Compliance

### Components Used
- ✅ Card (with custom colors)
- ✅ Button
- ✅ FloatingActionButton
- ✅ TopAppBar
- ✅ Scaffold
- ✅ CircularProgressIndicator
- ✅ Switch
- ✅ IconButton
- ✅ Icon (filled style)
- ✅ Surface
- ✅ Text (Material3 typography)

### Design System
- ✅ Uses Material3 color system
- ✅ Uses Material3 typography scale
- ✅ Follows elevation guidelines
- ✅ Consistent spacing scale
- ✅ Dark theme applied throughout

---

## Quick Color Codes

```kotlin
// Use in code:
Color(0xFF0F1115)  // DarkBackground
Color(0xFF1C1F24)  // CardBackground
Color(0xFF4CAF50)  // BatteryGreen
Color(0xFF9AA0A6)  // SecondaryText
Color(0xFFFF6B6B)  // ErrorRed
Color(0xFFFFFFFF)  // White
```

---

## Design Files

For design specifications and visual mockups, refer to:
- `DESIGN_IMPLEMENTATION.md` - Implementation details
- `COMPONENT_STRUCTURE.md` - Component layouts
- `QUICKSTART_IMPLEMENTATION.md` - Quick reference

---

## End Notes

This design system provides:
✅ Modern, clean dark theme
✅ Consistent visual language
✅ WCAG AA accessibility compliance
✅ Material3 design system compliance
✅ Mobile-optimized layout
✅ Professional appearance
✅ Easy maintenance and updates
