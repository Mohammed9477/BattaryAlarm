package com.example.battaryalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

            val batteryPct = (level / scale.toFloat() * 100).toInt()
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING && plugged > 0

            // Send broadcast to service with battery info
            val broadcastIntent = Intent(BATTERY_INFO_ACTION)
            broadcastIntent.putExtra("battery_level", batteryPct)
            broadcastIntent.putExtra("is_charging", isCharging)
            context?.sendBroadcast(broadcastIntent)
        }
    }

    companion object {
        const val BATTERY_INFO_ACTION = "com.example.battaryalarm.BATTERY_INFO"
    }
}
