# 🧪 Complete Testing Guide for Battery Alarm Fix

## ✅ Pre-Test Checklist

- [ ] All code changes applied
- [ ] Project rebuilt successfully
- [ ] App installed on device/emulator
- [ ] Logcat open and filtering "BatteryService"
- [ ] At least one alarm created in the app

---

## 🔌 TEST 1: Basic Alert Trigger

### Scenario: Create alarm and charge to threshold

**Steps**:
1. Open the app
2. Tap "+" to create alarm
3. Select: **70%** battery, **Vibrate** mode
4. Tap "Save Alarm"
5. Open Logcat: Filter for "AlarmPreferences"
6. Should see: `"Saved 1 alarms"`
7. Plug in charger at ~50% battery
8. Open Logcat: Filter for "BatteryService"
9. Watch battery increase: 50% → 60% → 70%
10. **When it hits 70%:**
    - [ ] Phone VIBRATES
    - [ ] Logcat shows: `"TRIGGER ALERT! Alarm ID=..."`
    - [ ] Alert continues vibrating

**Expected Logcat Output**:
```
Battery Update: level=50%, charging=true, status=2, plugged=2
Battery Update: level=60%, charging=true, status=2, plugged=2
Battery Update: level=70%, charging=true, status=2, plugged=2
TRIGGER ALERT! Alarm ID=..., type=VIBRATE
```

---

## 📞 TEST 2: Ring Alert Instead of Vibrate

### Scenario: Test ring mode

**Steps**:
1. Create alarm: **75%**, **Ring** mode
2. Plug charger at 60% battery
3. Let battery increase to 75%
4. **Should hear alarm tone** (or see notification in Logcat)

**Expected Result**: Phone RINGS at 75%

---

## 🔋 TEST 3: Alert Already Above Threshold

### Scenario: Battery is already above threshold when plugged in

**Setup**: 
- Battery currently at 80%
- Create alarm for 70%

**Steps**:
1. Create alarm: **70%**, **Vibrate** mode
2. Your battery is already 80%
3. Plug in charger
4. **Should IMMEDIATELY vibrate** (within 1-2 seconds)
5. Check Logcat:
   - Should see: `"Charger plugged in! Checking alarms..."`
   - Should see: `"TRIGGER ALERT!"`

**Expected Result**: Immediate vibration

---

## ⏱️ TEST 4: Alert Continues While Charging

### Scenario: Battery keeps increasing, alert keeps playing

**Steps**:
1. Alert is vibrating at 70%
2. Don't unplug charger
3. Battery increases: 80% → 90% → 100%
4. Check Logcat - should show multiple battery updates
5. **Alert should continuously vibrate**

**Expected Result**: Alert doesn't stop until charger is unplugged

---

## 🔌➡️ TEST 5: Alert Stops on Unplug

### Scenario: Alert stops immediately when charger removed

**Steps**:
1. Battery at 75%, plugged in, alert vibrating
2. Unplug charger
3. **Vibration stops immediately** (within 1 second)
4. Check Logcat: Should see `"Charger unplugged! Stopping alert..."`

**Expected Result**: Vibration/ring STOPS immediately

---

## 🔄 TEST 6: Plug Again → Alert Triggers Again

### Scenario: Alert can trigger multiple times per session

**Steps**:
1. Battery at 50%, unplugged (no alert)
2. Plug charger
3. Battery reaches 70% → **ALERT STARTS** ✓
4. Unplug charger at 70% → **ALERT STOPS** ✓
5. Plug charger again at 60%
6. Battery increases to 70% again → **ALERT STARTS AGAIN** ✓

**Expected Result**: Alert triggers twice (once per charge cycle)

---

## 📱 TEST 7: App in Background

### Scenario: Alert works even when app is in background

**Steps**:
1. Create alarm: 70%, Vibrate
2. Plug in charger at 50%
3. Press Home button (minimize app)
4. Let battery increase to 70%
5. **Should vibrate even though app is minimized**
6. Go back to app - should show the alarm in list

**Expected Result**: Foreground service keeps running, alert triggers

---

## 🔐 TEST 8: App Closed

### Scenario: Service continues running after app is closed

**Steps**:
1. Create alarm: 70%, Ring
2. Close the app (swipe away or back button)
3. Plug in charger at 50%
4. Let battery increase to 70%
5. **Should ring even though app is closed**

**Expected Result**: Service keeps running in background

---

## 🔇 TEST 9: Multiple Alarms

### Scenario: App handles multiple alarms correctly

**Steps**:
1. Create Alarm 1: 70%, Vibrate
2. Create Alarm 2: 80%, Ring
3. Plug in charger at 50%
4. Battery increases: 50% → 70% → 80% → ...
5. At 70%: **Check Logcat** - should see `"Checking 2 alarms"`
6. **Should vibrate** (first alarm triggered)
7. **Should NOT ring yet** (second alarm threshold not reached)
8. Battery increases to 80%
9. **Should still vibrate** (already alerting for first alarm)
10. Unplug → all alerts stop

**Expected Result**: 
- Only first matching alarm triggers
- Doesn't trigger multiple alarms simultaneously
- Stops all alerts on unplug

---

## 📊 TEST 10: Logcat Verification

### How to verify everything is working from Logcat

**Steps**:
1. Open Android Studio
2. Open Logcat (View → Tool Windows → Logcat)
3. Filter: `"BatteryService"`
4. Plug in charger
5. Watch for these messages:

```
✓ Battery Update: level=XX%, charging=true
✓ Checking N alarms
✓ Alarm ID=..., threshold=70%, active=true
✓ TRIGGER ALERT! Alarm ID=..., type=VIBRATE
✓ Vibration effect started
✓ Charger unplugged! Stopping alert...
```

---

## ❌ TROUBLESHOOTING

### Problem: Alert doesn't trigger at any battery level

**Check**:
1. [ ] Alarm created? (See message: "Added alarm: ID=...")
2. [ ] Charger actually connected? (See message: "charging=true")
3. [ ] Battery reached threshold? (See: "level=70%" for 70% alarm)
4. [ ] Vibrator initialized? (See: "Vibrator initialized: true")

**Common Causes**:
- Charger not detected (check `plugged=0`)
- Battery stuck at one level (try manually increasing)
- Vibrator disabled on device (check Settings → Sound & Vibration)

---

### Problem: Alert triggers too many times

**Check**:
1. [ ] Is charger still plugged in? (Check `charging=true`)
2. [ ] Battery increasing? (Check `level` in messages)

**Expected**: Should only trigger once per battery level crossing

---

### Problem: Alert doesn't stop on unplug

**Check**:
1. [ ] Did you actually unplug? (Check `charging=false`)
2. [ ] See message "Charger unplugged"?

**If no message**: Charger might not be detected as unplugged

---

### Problem: Vibration is very weak or not felt

**Try**:
1. [ ] Enable maximum vibration in device settings
2. [ ] Switch to Ring mode to verify alert is working
3. [ ] Check if vibrator is initialized (Logcat: "Vibrator initialized: true")

---

## 📋 Test Results Checklist

| Test | Expected | Result | ✓/✗ |
|------|----------|--------|-----|
| Basic Vibrate Trigger | Vibrates at threshold | | |
| Ring Mode | Rings at threshold | | |
| Already Above Threshold | Immediate alert | | |
| Alert Continues | Vibrates while charging | | |
| Unplug Stops Alert | Alert stops immediately | | |
| Plug Again | Alert triggers again | | |
| App in Background | Works when minimized | | |
| App Closed | Works when closed | | |
| Multiple Alarms | Only first triggers | | |
| Logcat Shows Updates | Can see all messages | | |

**Overall Status**: _________________

---

## 🎯 Final Verification

After all tests pass:

1. [ ] All 10 tests completed successfully
2. [ ] Logcat shows expected messages
3. [ ] No crashes or exceptions
4. [ ] App can be killed and restarted
5. [ ] Multiple alarm creation works
6. [ ] Data persists after app restart

**App is PRODUCTION READY!** ✅

---

## 📞 Debug Commands

```bash
# View all logs
adb logcat | grep "BatteryService\|AlarmManager\|AlarmPreferences\|CreateAlarmScreen"

# Clear logs
adb logcat -c

# View only errors
adb logcat | grep ERROR

# View service status
adb shell dumpsys activity services com.example.battaryalarm

# View SharedPreferences (requires root or emulator)
adb shell run-as com.example.battaryalarm cat /data/data/com.example.battaryalarm/shared_prefs/battery_alarm_prefs.xml
```

---

**Happy Testing!** 🚀
