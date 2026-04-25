package com.example.battaryalarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battaryalarm.data.Alarm
import com.example.battaryalarm.ui.viewmodel.AlarmViewModel
import com.example.battaryalarm.ui.theme.BatteryGreen

@Composable
fun MainScreen(
    viewModel: AlarmViewModel,
    onNavigateToCreate: () -> Unit
) {
    val alarms by viewModel.alarms.collectAsState()
    val activeCount = alarms.count { it.isActive }

    Scaffold(
        containerColor = Color(0xFF0F1115),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = BatteryGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔥 Header
            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Active Chip
            ActiveChip(activeCount)

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Alarm List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alarms) { alarm ->
                    ModernAlarmCard(
                        alarm = alarm,
                        onDelete = { id -> viewModel.deleteAlarm(id) },
                        onToggleActive = { id -> viewModel.toggleAlarm(id) }
                    )
                }
            }
        }
    }
}
@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Battery Alarm",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Get alerted when your battery reaches the selected level.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Placeholder battery icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BatteryGreen.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🔋")
        }
    }
}

@Composable
fun ActiveChip(count: Int) {
    Row(
        modifier = Modifier
            .background(Color(0xFF1C1F24), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Active",
            color = BatteryGreen
        )
    }
}
@Composable
fun ModernAlarmCard(
    alarm: Alarm,
    onDelete: (Long) -> Unit,
    onToggleActive: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1F24)
        )
    ) {
        Box {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🔵 Circular progress
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = alarm.percentage / 100f,
                        color = BatteryGreen,
                        strokeWidth = 6.dp,
                        modifier = Modifier.size(60.dp)
                    )

                    Text(
                        "${alarm.percentage}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alarm.alertType.displayName,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.isActive) Color.White else Color.Gray
                    )

                    Text(
                        text = "Alarm when charging reaches ${alarm.percentage}%",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // 🔥 3 dots menu button
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                }
            }

            // 🔥 Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                DropdownMenuItem(
                    text = {
                        Text(if (alarm.isActive) "Disable" else "Enable")
                    },
                    onClick = {
                        expanded = false
                        onToggleActive(alarm.id)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDelete(alarm.id)
                    }
                )
            }
        }
    }
}