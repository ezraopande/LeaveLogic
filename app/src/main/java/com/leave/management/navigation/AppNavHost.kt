package com.leave.management.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leave.management.ui.screens.SplashScreen.SplashScreen
import com.leave.management.ui.screens.admin.AddEmployeeScreen
import com.leave.management.ui.screens.admin.AdminAccount
import com.leave.management.ui.screens.admin.AdminDashboard
import com.leave.management.ui.screens.admin.AdminLoginScreen
import com.leave.management.ui.screens.admin.AdminSettingScreen
import com.leave.management.ui.screens.admin.AllLeaves
import com.leave.management.ui.screens.admin.EmployeeDetailsScreen
import com.leave.management.ui.screens.admin.Employees
import com.leave.management.ui.screens.admin.RegisterScreen
import com.leave.management.ui.screens.admin.ViewEmployeesScreen
import com.leave.management.ui.screens.admin.usersessions.CheckLoginStatus
import com.leave.management.ui.screens.employee.ApplyLeaveScreen
import com.leave.management.ui.screens.employee.EmployeeAccount
import com.leave.management.ui.screens.employee.EmployeeDashboard
import com.leave.management.ui.screens.employee.EmployeeSettingScreen
import com.leave.management.ui.screens.employee.LoginScreen
import com.leave.management.ui.screens.employee.leaves.AllMyLeaves


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
               navController: NavHostController = rememberNavController(),
               startDestination:String= ROUTE_SPLASHSCREEN
) {
    CheckLoginStatus(navController)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){

        composable(ROUTE_LOGIN){
            LoginScreen(navController)
        }

        composable(ROUTE_ADMINACCOUNT){
            AdminAccount(navController = navController)

        }

        composable(ROUTE_REGISTER){
            RegisterScreen(navController)
        }

        composable(ROUTE_MYLEAVES){
            AllMyLeaves(navController)
        }
        composable(ROUTE_APPLY){
            ApplyLeaveScreen(navController)
        }

        composable(ROUTE_ADMINLOGIN){
            AdminLoginScreen(navController)
        }

        composable(ROUTE_ADMINDASBOARD){
            AdminDashboard(navController)
        }


        composable(ROUTE_EMPLOYEEDASHBOARD){
            EmployeeDashboard(navController)
        }


        composable(ROUTE_VIEWLEAVEs){
            AllLeaves(navController)
        }

        composable(ROUTE_VIEWEMPLOYEES){
            ViewEmployeesScreen(navController)
        }

        composable(ROUTE_ADDEMPLOYEE){
            AddEmployeeScreen(navController)
        }

        composable(ROUTE_EMPLOYEEACCOUNT){
            EmployeeAccount(navController)
        }

        composable(ROUTE_ADMINSETTINGS){
            AdminSettingScreen(navController)
        }

        composable(ROUTE_EMPLOYEESETTINGS){
            EmployeeSettingScreen(navController)
        }

        composable(ROUTE_SPLASHSCREEN){
            SplashScreen(navController)
        }

        composable(
            "employeeDetails/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmployeeDetailsScreen(email = email, navController = navController)
        }







    }

}