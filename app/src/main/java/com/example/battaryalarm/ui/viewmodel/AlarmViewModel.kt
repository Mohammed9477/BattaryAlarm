package com.example.battaryalarm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.battaryalarm.data.Alarm
import com.example.battaryalarm.data.AlertType
import com.example.battaryalarm.data.AlarmPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : ViewModel() {
    private val preferences = AlarmPreferences(application)

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    private val _lastSelectedPercentage = MutableStateFlow(80)
    val lastSelectedPercentage: StateFlow<Int> = _lastSelectedPercentage.asStateFlow()

    init {
        loadAlarms()
        loadLastSelectedPercentage()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            _alarms.value = preferences.getAlarms()
        }
    }

    private fun loadLastSelectedPercentage() {
        viewModelScope.launch {
            _lastSelectedPercentage.value = preferences.getLastSelectedPercentage()
        }
    }

    fun addAlarm(percentage: Int, alertType: AlertType) {
        val newAlarm = Alarm(
            id = System.currentTimeMillis(),
            percentage = percentage,
            alertType = alertType,
            isActive = true
        )
        preferences.addAlarm(newAlarm)
        preferences.saveLastSelectedPercentage(percentage)
        loadAlarms()
    }

    fun deleteAlarm(id: Long) {
        preferences.deleteAlarm(id)
        loadAlarms()
    }

    fun toggleAlarm(id: Long) {
        val alarms = preferences.getAlarms().map {
            if (it.id == id) it.copy(isActive = !it.isActive)
            else it
        }
        preferences.saveAlarms(alarms)
        loadAlarms()
    }

    fun updateAlarmStatus(id: Long, isActive: Boolean) {
        viewModelScope.launch {
            val alarms = preferences.getAlarms()
            val alarm = alarms.find { it.id == id }
            if (alarm != null) {
                preferences.updateAlarm(alarm.copy(isActive = isActive))
                loadAlarms()
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
                return AlarmViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}