package com.example.battaryalarm.data

import java.io.Serializable

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
