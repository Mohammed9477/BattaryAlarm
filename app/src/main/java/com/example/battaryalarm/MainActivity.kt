package com.example.battaryalarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.battaryalarm.service.BatteryService
import com.example.battaryalarm.ui.navigation.AppNavigation
import com.example.battaryalarm.ui.theme.BattaryAlarmTheme
import com.example.battaryalarm.ui.theme.DarkBackground
import com.example.battaryalarm.ui.viewmodel.AlarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start service only once
        if (savedInstanceState == null) {
            android.util.Log.d("MainActivity", "Starting BatteryService...")
            val serviceIntent = Intent(this, BatteryService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }

        setContent {
            BattaryAlarmTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: AlarmViewModel = viewModel(
                        factory = AlarmViewModel.Factory(application)
                    )

                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}