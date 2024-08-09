package com.leave.management.ui.screens.admin

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.leave.management.navigation.ROUTE_ADDEMPLOYEE
import com.leave.management.navigation.ROUTE_ADMINDASBOARD
import com.leave.management.navigation.ROUTE_ADMINSETTINGS
import com.leave.management.navigation.ROUTE_VIEWLEAVEs
import com.leave.management.navigation.ROUTE_VIEWEMPLOYEES
import com.leave.management.navigation.ROUTE_ADMINLOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingScreen(navController: NavHostController) {
    var presses by remember { mutableIntStateOf(0) }
    val mContext = LocalContext.current
    var search by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showTermsPrivacyDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(Color(0xFF018786)),
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
            )
        },
        bottomBar = { AdminNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFF5F5F5))
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SettingCard(
                title = "Terms and Privacy Policy",
                description = "Read Terms and Privacy Policy",
                icon = Icons.Filled.Info,
                onClick = { showTermsPrivacyDialog = true }
            )
            if (showTermsPrivacyDialog) {
                TermsPrivacyDialog(showTermsPrivacyDialog) { showTermsPrivacyDialog = false }
            }

            SettingCard(
                title = "Change Password",
                icon = Icons.Filled.ArrowForwardIos,
                onClick = { showPasswordDialog = true }
            )
            if (showPasswordDialog) {
                ChangePasswordDialog(showPasswordDialog, newPassword, { newPassword = it }) { showPasswordDialog = false }
            }

            SettingCard(
                title = "Log Out",
                icon = Icons.Filled.ExitToApp,
                onClick = { showLogoutDialog = true }
            )
            if (showLogoutDialog) {
                LogoutDialog(showLogoutDialog, navController) { showLogoutDialog = false }
            }

            ExtraSettingsCard(mContext)

            AboutMeCard()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar(
        contentColor = Color.White,
        backgroundColor = Color(0xFF018786),
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) {
        BottomNavigationItem(
            label = "DASHBOARD",
            icon = Icons.Filled.Dashboard,
            onClick = { navController.navigate(ROUTE_ADMINDASBOARD) }
        )
        BottomNavigationItem(
            label = "LEAVES",
            icon = Icons.AutoMirrored.Filled.Notes,
            onClick = { navController.navigate(ROUTE_VIEWLEAVEs) }
        )
        BottomNavigationItem(
            label = "ADD EMPLOYEE",
            icon = Icons.Filled.PersonAdd,
            onClick = { navController.navigate(ROUTE_ADDEMPLOYEE) }
        )
        BottomNavigationItem(
            label = "EMPLOYEES",
            icon = Icons.Filled.People,
            onClick = { navController.navigate(ROUTE_VIEWEMPLOYEES) }
        )
        BottomNavigationItem(
            label = "SETTINGS",
            icon = Icons.Filled.Settings,
            onClick = { navController.navigate(ROUTE_ADMINSETTINGS) }
        )
    }
}

@Composable
fun BottomNavigationItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
//        modifier = Modifier.weight(1f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

@Composable
fun SettingCard(title: String, description: String = "", icon: ImageVector, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (description.isNotEmpty()) {
                    Text(text = description, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Composable
fun TermsPrivacyDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Terms and Privacy Policy") },
            text = {
                Column {
                    Text("1. Leave application stores application data on Firebase server.")
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    Text("2. We are not responsible for the loss of data in case of any issues.")
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    Text("3. Keep the leave app up to date to enjoy new features.")
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(Color.Cyan), onClick = onDismiss) {
                    Text("OK", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun ChangePasswordDialog(showDialog: Boolean, newPassword: String, onPasswordChange: (String) -> Unit, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Change Password") },
            text = {
                Column {
                    Text("Please enter your new password:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = onPasswordChange,
                        trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        placeholder = { Text("New Password") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(Color.Cyan), onClick = onDismiss) {
                    Text("Change", color = Color.Black)
                }
            },
            dismissButton = {
                Button(colors = ButtonDefaults.buttonColors(Color.Red), onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LogoutDialog(showDialog: Boolean, navController: NavHostController, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Confirm") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(Color.Cyan), onClick = {
                    onDismiss()
                    navController.navigate(ROUTE_ADMINLOGIN)
                }) {
                    Text("Yes", color = Color.Black)
                }
            },
            dismissButton = {
                Button(colors = ButtonDefaults.buttonColors(Color.Red), onClick = onDismiss) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun ExtraSettingsCard(mContext: Context) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Social", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Rate Us", fontSize = 14.sp)
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = "like")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Feedback", fontSize = 14.sp)
                IconButton(onClick = {
                    val feedbackIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/email"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("your@email.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                    }
                    mContext.startActivity(feedbackIntent)
                }) {
                    Icon(imageVector = Icons.Filled.Feedback, contentDescription = "feedback")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Share", fontSize = 14.sp)
                IconButton(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                    }
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share via"))
                }) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "share")
                }
            }
        }
    }
}

@Composable
fun AboutMeCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "About Me", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "All rights reserved @stevdza-san", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
