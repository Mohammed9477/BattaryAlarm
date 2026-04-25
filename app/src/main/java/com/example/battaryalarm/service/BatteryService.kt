package com.example.battaryalarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.battaryalarm.data.AlarmPreferences
import com.example.battaryalarm.util.AlarmManager

class BatteryService : Service() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var preferences: AlarmPreferences
    private lateinit var notificationManager: NotificationManager
    private lateinit var vibrator: Vibrator

    private var batteryLevel = 0
    private var isCharging = false
    private var wasCharging = false
    private var alertingAlarmId: Long? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "battery_alarm_channel"
        private const val TAG = "BatteryService"
        const val ACTION_STOP_ALERT = "STOP_ALERT"
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                try {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

                    batteryLevel = if (scale > 0) (level * 100 / scale) else 0

                    // ✅ Correct charging detection
                    wasCharging = isCharging
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL

                    Log.d(TAG, "Battery: $batteryLevel%, charging=$isCharging")

                    // 🔌 Plugged in
                    if (!wasCharging && isCharging) {
                        Log.d(TAG, "Charger plugged → check alarms")
                        checkAndTriggerAlerts()
                    }

                    // 🔋 While charging
                    else if (isCharging) {
                        checkAndTriggerAlerts()
                    }

                    // 🔌 Unplugged → STOP
                    else if (wasCharging && !isCharging) {
                        Log.d(TAG, "Charger unplugged → STOP ALERT")
                        stopAlert()
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Receiver error", e)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        alarmManager = AlarmManager(this)
        preferences = AlarmPreferences(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        createNotificationChannel()

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // ✅ Manual STOP support
        if (intent?.action == ACTION_STOP_ALERT) {
            Log.d(TAG, "Manual stop triggered")
            stopAlert()
            return START_STICKY
        }

        try {
            startForeground(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            Log.e(TAG, "Foreground error", e)
        }

        return START_STICKY
    }

    private fun checkAndTriggerAlerts() {
        if (!isCharging) return

        val alarms = preferences.getAlarms()

        Log.d(TAG, "Checking ${alarms.size} alarms at $batteryLevel%")

        for (alarm in alarms) {

            Log.d(TAG, "Alarm ${alarm.percentage}% active=${alarm.isActive}")

            if (alarm.isActive &&
                batteryLevel >= alarm.percentage &&
                alertingAlarmId == null
            ) {
                Log.d(TAG, "🔥 TRIGGER ALERT ${alarm.percentage}%")

                alertingAlarmId = alarm.id
                startVibration()

                break
            }
        }
    }

    // ✅ START vibration (loop)
    private fun startVibration() {
        val pattern = longArrayOf(0, 1000, 500)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, 0)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(pattern, 0)
        }
    }

    // ✅ STOP vibration properly
    private fun stopAlert() {
        Log.d(TAG, "Stopping alert")

        vibrator.cancel()
        alarmManager.stopAlert()

        alertingAlarmId = null
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

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Battery Alarm")
            .setContentText("Monitoring battery...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(batteryReceiver)
        stopAlert()
        alarmManager.release()

        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}