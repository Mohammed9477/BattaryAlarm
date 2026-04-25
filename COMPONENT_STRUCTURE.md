# Component Structure - Battery Alarm UI

## Screen Hierarchy

```
MainScreen (Home/Alarm List)
├── Header Section
│   ├── Title: "Battery Alarm"
│   ├── Subtitle: Description text
│   ├── Battery Icon (right side)
│   └── Active Alarms Chip (if alarms exist)
│
├── Empty State (if no alarms)
│   ├── "No Alarms Yet" message
│   └── "Create Alarm" button
│
└── Alarm List (if alarms exist)
    └── AlarmCardComponent (repeating)
        ├── Circular Progress (64dp, showing %)
        ├── Title: Alarm Type (Ring/Vibrate)
        ├── Subtitle: "Alarm when battery reaches X%"
        ├── Status Switch (toggle on/off)
        └── Delete Button (red icon)

CreateAlarmScreen
├── TopAppBar (Green background)
│   ├── Title: "Create Alarm"
│   └── Back Button
│
├── Battery Level Section
│   ├── Title: "Battery Level"
│   ├── Description text
│   └── PercentageChip (x6)
│       ├── 70%, 75%, 80%, 85%, 90%, 95%
│       ├── Selected: Green background + white text
│       └── Unselected: Dark background + gray text
│
├── Alert Type Section
│   ├── Title: "Alert Type"
│   ├── Description text
│   └── AlertTypeCard (x2)
│       ├── Ring
│       │   ├── Radio indicator
│       │   ├── Notifications icon
│       │   ├── Title & Description
│       │   └── Selected: Light green background + border
│       │
│       └── Vibrate
│           ├── Radio indicator
│           ├── Vibration icon
│           ├── Title & Description
│           └── Selected: Light green background + border
│
└── Create Button (Full width, green, 52dp)
```

## Component Details

### AlarmCardComponent
**Location**: `MainScreen.kt`

```kotlin
@Composable
fun AlarmCardComponent(
    alarm: Alarm,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onStatusChange: (Boolean) -> Unit = {}
)
```

**Features**:
- Circular progress indicator with percentage
- Alarm type title
- Description text
- Toggle switch
- Delete button
- **Colors**:
  - Background: `CardBackground` (#1C1F24)
  - Progress: `BatteryGreen` (#4CAF50)
  - Delete button: `ErrorRed` (#FF6B6B)
- **Dimensions**: 18dp rounded corners, 16dp padding

### PercentageChip
**Location**: `CreateAlarmScreen.kt`

```kotlin
@Composable
fun PercentageChip(
    percentage: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
)
```

**Features**:
- Selectable battery percentage options
- Selected state: Green background, white text
- Unselected state: Dark background, gray text
- **Colors**:
  - Selected: `BatteryGreen` (#4CAF50)
  - Unselected: `CardBackground` (#1C1F24)
  - Text: White (selected) / `SecondaryText` (unselected)
- **Dimensions**: 12dp rounded corners, 50dp height

### AlertTypeCard
**Location**: `CreateAlarmScreen.kt`

```kotlin
@Composable
fun AlertTypeCard(
    alertType: AlertType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
)
```

**Features**:
- Shows alert type with icon
- Radio button indicator (checked/unchecked)
- Title and description
- Selected state: Light green background + 2dp border
- Unselected state: Dark card background
- **Icons**:
  - Ring: `Icons.Default.Notifications`
  - Vibrate: `Icons.Default.Vibration`
  - Radio: `Icons.Default.RadioButtonChecked/Unchecked`
- **Colors**:
  - Selected: `BatteryGreen.copy(alpha = 0.15f)`
  - Border: `BatteryGreen` (2dp, selected only)
  - Icons: `BatteryGreen` (selected) / `SecondaryText` (unselected)
- **Dimensions**: 14dp rounded corners, 16dp padding

## Theme Colors

```kotlin
val BatteryGreen = Color(0xFF4CAF50)      // Accent green
val DarkBackground = Color(0xFF0F1115)     // Very dark background
val CardBackground = Color(0xFF1C1F24)     // Dark card background
val SurfaceColor = Color(0xFF2A2A2A)       // Alternative surface
val SecondaryText = Color(0xFF9AA0A6)      // Gray text
val ErrorRed = Color(0xFFFF6B6B)           // Error/delete red
```

## Icon Usage

**MainScreen**:
- `Icons.Default.Add` - FAB button
- `Icons.Default.Battery6Alert` - Header icon
- `Icons.Default.Delete` - Delete button in cards

**CreateAlarmScreen**:
- `Icons.AutoMirrored.Filled.ArrowBack` - Back button
- `Icons.Default.RadioButtonChecked` - Selected radio
- `Icons.Default.RadioButtonUnchecked` - Unselected radio
- `Icons.Default.Notifications` - Ring alert icon
- `Icons.Default.Vibration` - Vibrate alert icon

## Spacing & Dimensions

- **Header padding**: 24dp
- **Card padding**: 16dp
- **Card corners**: 18dp (cards), 14dp (alert type), 12dp (percentage chips)
- **Component spacing**: 12dp between items
- **Screen gaps**: 32dp between sections
- **FAB padding**: 16dp from edges
- **Button height**: 52dp
- **Progress circle size**: 64dp
- **Switch size**: 50dp width, 24dp height
- **Icon sizes**: 24dp (radio), 28dp (alert type), 32dp (battery icon)

## State Management

All components use proper Compose state patterns:
- `mutableStateOf()` for selections
- `collectAsState()` for ViewModel flows
- Callbacks (`onClick`, `onStatusChange`, `onDelete`) for parent communication

No external state management required - pure Compose patterns used throughout.
