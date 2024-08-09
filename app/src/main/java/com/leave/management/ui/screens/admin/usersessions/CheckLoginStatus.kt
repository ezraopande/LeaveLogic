package com.leave.management.ui.screens.admin.usersessions

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.leave.management.navigation.ROUTE_ADMINDASBOARD
import com.leave.management.navigation.ROUTE_ADMINLOGIN

@Composable
fun CheckLoginStatus(navController: NavHostController) {
    val mContext = LocalContext.current
    val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    LaunchedEffect(Unit) {
        if (isLoggedIn) {
            navController.navigate(ROUTE_ADMINDASBOARD) {
                popUpTo(ROUTE_ADMINLOGIN) { inclusive = true }
            }
        }
    }
}
