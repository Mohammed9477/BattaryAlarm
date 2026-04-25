package com.example.battaryalarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battaryalarm.data.AlertType
import com.example.battaryalarm.ui.theme.BatteryGreen
import com.example.battaryalarm.ui.viewmodel.AlarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlarmScreen(
    viewModel: AlarmViewModel,
    onBack: () -> Unit
) {
    var selectedPercentage by remember { mutableStateOf(80) }
    var selectedAlertType by remember { mutableStateOf(AlertType.RING) }

    val options = listOf(65, 70, 75, 80, 85)

    Scaffold(
        containerColor = Color(0xFF0F1115),
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Alarm", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1115)
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 🔋 Battery Level Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Battery Level",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Select the battery percentage at which the alarm should trigger.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { value ->
                        PercentageChip(
                            value = value,
                            selected = value == selectedPercentage,
                            onClick = { selectedPercentage = value }
                        )
                    }
                }
            }

            // 🔔 Alert Type Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text(
                    text = "Alert Type",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Choose how you want to be alerted.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                AlertTypeCard(
                    title = "Ring",
                    description = "Play a continuous alarm sound until unplugged.",
                    selected = selectedAlertType == AlertType.RING,
                    icon = Icons.Default.Notifications
                ) {
                    selectedAlertType = AlertType.RING
                }

                AlertTypeCard(
                    title = "Vibrate",
                    description = "Vibrate continuously until unplugged.",
                    selected = selectedAlertType == AlertType.VIBRATE,
                    icon = Icons.Default.PlayArrow
                ) {
                    selectedAlertType = AlertType.VIBRATE
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 💾 Save Button
            Button(
                onClick = {
                    viewModel.addAlarm(selectedPercentage, selectedAlertType)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BatteryGreen
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Save Alarm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun PercentageChip(
    value: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) BatteryGreen else Color.Transparent)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "$value%",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun AlertTypeCard(
    title: String,
    description: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                Color(0xFF1E2A1F)
            else
                Color(0xFF1C1F24)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BatteryGreen
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(icon, contentDescription = null, tint = BatteryGreen)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
