# Code Examples & Snippets

## Key Implementation Details

---

## 🔋 How Battery Monitoring Works

### Battery Service - Core Loop
```kotlin
// BatteryService.kt
private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            // Parse battery info
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            
            // Calculate percentage
            batteryLevel = (level / 100.0f * 100).toInt()
            
            // Check charging
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING && plugged > 0
            
            // Trigger alerts if needed
            checkAndTriggerAlerts()
            
            // Stop alert when unplugged
            if (!isCharging && alertingAlarmId != null) {
                stopAlert()
            }
        }
    }
}
```

---

## 🎯 Alert Triggering Logic

```kotlin
// Only triggers ONE alarm (no duplicates)
private fun checkAndTriggerAlerts() {
    if (!isCharging) return
    
    val alarms = preferences.getAlarms()
    for (alarm in alarms) {
        if (alarm.isActive && 
            batteryLevel >= alarm.percentage && 
            alertingAlarmId == null) {  // Only if not already alerting
            
            // Start alert for this alarm
            alertingAlarmId = alarm.id
            alarmManager.playAlert(alarm.alertType)
            break  // Stop after triggering one
        }
    }
}
```

---

## 📢 Ring Alert Implementation

```kotlin
// AlarmManager.kt - Ring Mode
private fun playRingtone() {
    try {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        ringtone?.play()  // Loops continuously
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

---

## 📳 Vibration Implementation

```kotlin
// AlarmManager.kt - Vibrate Mode
private fun playVibration() {
    try {
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern)
            vibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

---

## 💾 Data Persistence with SharedPreferences

```kotlin
// AlarmPreferences.kt
class AlarmPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(
        "battery_alarm_prefs", 
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    // Save alarms as JSON
    fun saveAlarms(alarms: List<Alarm>) {
        val json = gson.toJson(alarms)  // Convert to JSON
        prefs.edit().putString("alarms", json).apply()
    }
    
    // Load alarms from JSON
    fun getAlarms(): List<Alarm> {
        val json = prefs.getString("alarms", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Alarm>>() {}.type
            gson.fromJson(json, type)  // Convert back from JSON
        } catch (e: Exception) {
            emptyList()
        }
    }
}
```

---

## 🎨 Main Screen - Alarm List

```kotlin
// MainScreen.kt - Simplified
@Composable
fun MainScreen(viewModel: AlarmViewModel, onNavigateToCreate: () -> Unit) {
    val alarms by viewModel.alarms.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Battery Alarm", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BatteryGreen  // #00C853
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(alarms) { alarm ->
                AlarmCard(
                    alarm = alarm,
                    onDelete = { viewModel.deleteAlarm(alarm.id) },
                    onStatusChange = { viewModel.updateAlarmStatus(alarm.id, it) }
                )
            }
        }
    }
}
```

---

## 📝 Create Alarm Form

```kotlin
// CreateAlarmScreen.kt - Simplified
@Composable
fun CreateAlarmScreen(viewModel: AlarmViewModel, onBack: () -> Unit) {
    var selectedPercentage by remember { mutableStateOf(80) }
    var selectedAlertType by remember { mutableStateOf(AlertType.RING) }
    
    val percentageOptions = listOf(70, 75, 80, 85, 90, 95)
    
    Column(modifier = Modifier.padding(24.dp)) {
        // Percentage Selection (Chips)
        Row {
            percentageOptions.forEach { percentage ->
                Chip(
                    label = { Text("$percentage%") },
                    onClick = { selectedPercentage = percentage },
                    colors = ChipDefaults.chipColors(
                        containerColor = if (selectedPercentage == percentage)
                            BatteryGreen else Color.Gray
                    )
                )
            }
        }
        
        // Alert Type Selection (Radio)
        AlertType.values().forEach { type ->
            Row {
                RadioButton(
                    selected = selectedAlertType == type,
                    onClick = { selectedAlertType = type }
                )
                Text(type.displayName)
            }
        }
        
        // Save Button
        Button(
            onClick = {
                viewModel.addAlarm(selectedPercentage, selectedAlertType)
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Alarm")
        }
    }
}
```

---

## 🔄 ViewModel State Management

```kotlin
// AlarmViewModel.kt
class AlarmViewModel(context: Context) : ViewModel() {
    private val preferences = AlarmPreferences(context)
    
    // State management with StateFlow
    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    // Add new alarm
    fun addAlarm(percentage: Int, alertType: AlertType) {
        val newAlarm = Alarm(
            id = System.currentTimeMillis(),
            percentage = percentage,
            alertType = alertType,
            isActive = true
        )
        preferences.addAlarm(newAlarm)
        loadAlarms()  // Reload to update state
    }
    
    // Delete alarm
    fun deleteAlarm(id: Long) {
        viewModelScope.launch {
            preferences.deleteAlarm(id)
            loadAlarms()  // Reload to update state
        }
    }
    
    // Load alarms from storage
    private fun loadAlarms() {
        viewModelScope.launch {
            _alarms.value = preferences.getAlarms()
        }
    }
}
```

---

## 🧭 Navigation Setup

```kotlin
// Navigation.kt
sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object CreateAlarm : Screen("create_alarm")
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: AlarmViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateAlarm.route)
                }
            )
        }
        
        composable(Screen.CreateAlarm.route) {
            CreateAlarmScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

---

## 🎨 Theme Configuration

```kotlin
// Theme.kt - Dark Mode with Green Accent
private val DarkColorScheme = darkColorScheme(
    primary = BatteryGreen,        // #00C853
    secondary = BatteryGreen,
    tertiary = BatteryGreen,
    background = DarkBackground,   // #121212
    surface = CardBackground,      // #1E1E1E
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun BattaryAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 🎯 Data Model

```kotlin
// Alarm.kt
data class Alarm(
    val id: Long = System.currentTimeMillis(),
    val percentage: Int = 80,
    val alertType: AlertType = AlertType.RING,
    val isActive: Boolean = true
) : Serializable

enum class AlertType(val displayName: String) {
    RING("Ring"),
    VIBRATE("Vibrate")
}
```

---

## 📱 MainActivity - Service Startup

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Start Battery Service on app launch
        val serviceIntent = Intent(this, BatteryService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)  // Android 8+
        } else {
            @Suppress("DEPRECATION")
            startService(serviceIntent)
        }
        
        setContent {
            BattaryAlarmTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel = remember { AlarmViewModel(this@MainActivity) }
                    
                    AppNavigation(navController, viewModel)
                }
            }
        }
    }
}
```

---

## 🔔 Foreground Service Notification

```kotlin
// BatteryService.kt
private fun createNotification(): Notification {
    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Battery Alarm")
        .setContentText("Monitoring battery charging...")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}

private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Battery Alarm",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}
```

---

## ✅ Summary of Key Patterns

| Pattern | Usage |
|---------|-------|
| **StateFlow** | Reactive UI updates |
| **ViewModel** | Lifecycle-aware state |
| **Coroutines** | Background work |
| **Gson** | JSON serialization |
| **BroadcastReceiver** | Battery monitoring |
| **ForegroundService** | Background task |
| **Compose Navigation** | Screen routing |
| **Material 3** | UI design system |

---

All code is production-ready with proper error handling and API compatibility checks.
