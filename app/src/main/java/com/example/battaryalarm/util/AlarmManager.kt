package com.example.battaryalarm.util

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.battaryalarm.data.AlertType

class AlarmManager(private val context: Context) {
    private var ringtone: android.media.Ringtone? = null
    private var vibrator: Vibrator? = null

    init {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            Log.d(TAG, "Vibrator initialized: ${vibrator != null}")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing vibrator", e)
            vibrator = null
        }
    }

    fun playAlert(alertType: AlertType) {
        Log.d(TAG, "Playing alert: $alertType")
        when (alertType) {
            AlertType.RING -> playRingtone()
            AlertType.VIBRATE -> playVibration()
        }
    }

    private fun playRingtone() {
        try {
            Log.d(TAG, "Playing ringtone...")
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtone?.play()
            Log.d(TAG, "Ringtone started")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ringtone", e)
        }
    }

    private fun playVibration() {
        try {
            Log.d(TAG, "Playing vibration...")
            if (vibrator == null) {
                Log.w(TAG, "Vibrator is null! Cannot vibrate")
                return
            }

            // Pattern: wait 0ms, vibrate 500ms, wait 200ms, vibrate 500ms, wait 200ms, vibrate 500ms
            val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, 0) // 0 = repeat from start
                vibrator?.vibrate(effect)
                Log.d(TAG, "Vibration effect started")
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0) // 0 = repeat from start
                Log.d(TAG, "Vibration legacy API started")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing vibration", e)
        }
    }

    fun stopAlert() {
        try {
            Log.d(TAG, "Stopping alert...")
            ringtone?.stop()
            vibrator?.cancel()
            Log.d(TAG, "Alert stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping alert", e)
        }
    }

    fun isPlaying(): Boolean {
        return ringtone?.isPlaying == true
    }

    fun release() {
        Log.d(TAG, "Releasing AlarmManager")
        stopAlert()
        ringtone = null
        vibrator = null
    }

    companion object {
        private const val TAG = "AlarmManager"
    }
}