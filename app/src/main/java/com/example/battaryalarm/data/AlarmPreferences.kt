package com.example.battaryalarm.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAlarms(alarms: List<Alarm>) {
        try {
            val json = gson.toJson(alarms)
            prefs.edit().putString(ALARMS_KEY, json).apply()
            Log.d(TAG, "Saved ${alarms.size} alarms: $json")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving alarms", e)
        }
    }

    fun getAlarms(): List<Alarm> {
        return try {
            val json = prefs.getString(ALARMS_KEY, null)
            if (json == null) {
                Log.d(TAG, "No alarms found in SharedPreferences")
                return emptyList()
            }
            val type = object : TypeToken<List<Alarm>>() {}.type
            val alarms: List<Alarm> = gson.fromJson(json, type)
            Log.d(TAG, "Loaded ${alarms.size} alarms from SharedPreferences: $alarms")
            alarms
        } catch (e: Exception) {
            Log.e(TAG, "Error loading alarms", e)
            emptyList()
        }
    }

    fun deleteAlarm(id: Long) {
        try {
            val alarms = getAlarms().filter { it.id != id }
            saveAlarms(alarms)
            Log.d(TAG, "Deleted alarm ID=$id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting alarm", e)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        try {
            val alarms = getAlarms().toMutableList()
            val index = alarms.indexOfFirst { it.id == alarm.id }
            if (index >= 0) {
                alarms[index] = alarm
                saveAlarms(alarms)
                Log.d(TAG, "Updated alarm ID=${alarm.id}")
            } else {
                Log.w(TAG, "Alarm ID=${alarm.id} not found for update")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating alarm", e)
        }
    }

    fun addAlarm(alarm: Alarm) {
        try {
            val alarms = getAlarms().toMutableList()
            alarms.add(alarm)
            saveAlarms(alarms)
            Log.d(TAG, "Added alarm: ID=${alarm.id}, threshold=${alarm.percentage}%, type=${alarm.alertType}")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding alarm", e)
        }
    }

    fun getLastSelectedPercentage(): Int {
        return prefs.getInt(LAST_PERCENTAGE_KEY, 80)
    }

    fun saveLastSelectedPercentage(percentage: Int) {
        try {
            prefs.edit().putInt(LAST_PERCENTAGE_KEY, percentage).apply()
            Log.d(TAG, "Saved last selected percentage: $percentage%")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving last percentage", e)
        }
    }

    companion object {
        private const val PREFS_NAME = "battery_alarm_prefs"
        private const val ALARMS_KEY = "alarms"
        private const val LAST_PERCENTAGE_KEY = "last_percentage"
        private const val TAG = "AlarmPreferences"
    }
}
