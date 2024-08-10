package com.leave.management.ui.screens.employee

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_APPLY
import com.leave.management.navigation.ROUTE_EMPLOYEEACCOUNT
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.navigation.ROUTE_EMPLOYEESETTINGS
import com.leave.management.navigation.ROUTE_MYLEAVES
import com.leave.management.navigation.ROUTE_VIEWEMPLOYEES
import kotlinx.coroutines.tasks.await
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboard(navController: NavHostController){

    Scaffold(
        topBar = { },
        content = {


            Column(
                modifier = Modifier
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                EmployeeGreetingSection()
                EmployeeDashboardScreen(navController)



            }

        },
        bottomBar = {EmployeeBottomBar(navController = navController)}
    )




}



@RequiresApi(Build.VERSION_CODES.O)
fun getGreeting(): String {
    val currentTime = LocalTime.now()
    return when {
        currentTime.isBefore(LocalTime.NOON) -> "Good Morning"
        currentTime.isBefore(LocalTime.of(17, 0)) -> "Good Afternoon"
        else -> "Good Evening"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmployeeGreetingSection() {
    val greet = getGreeting()
    val mContext = LocalContext.current
    val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val user_email = sharedPreferences.getString("user_email", "") ?: ""

    var employeeName by remember { mutableStateOf("") }
    var employeeImage by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(user_email) {
        if (user_email.isNotEmpty()) {
            db.collection("employees")
                .whereEqualTo("email", user_email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        employeeName = document.getString("name") ?: ""
                        employeeImage = document.getString("image_url") ?: ""
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            androidx.compose.material.Text(
                text = if (employeeName.isNotEmpty()) "Hi, $employeeName" else "Loading...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            androidx.compose.material.Text(
                text = greet,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        AsyncImage(
            model = employeeImage,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xffF3F9F8))
        )
    }
}




@Composable
fun EmployeeDashboardScreen(navController: NavHostController,) {

    val context = LocalContext.current
    val email = getEmailFromPreferences(context) ?: ""
    var pendingLeaveCount by remember { mutableStateOf(0) }
    var approvedLeaveCount by remember { mutableStateOf(0) }
    var rejectedLeaveCount by remember { mutableStateOf(0) }
    var leaveCount by remember { mutableStateOf(0) }

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            pendingLeaveCount = getPendingLeaveCount(email)
        }

        if (email.isNotEmpty()) {
            approvedLeaveCount = getApprovedLeaveCount(email)
        }

        if (email.isNotEmpty()) {
            rejectedLeaveCount = getRejectedLeaveCount(email)
        }

        if (email.isNotEmpty()) {
            leaveCount = getLeaveCount(email)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                EmployeeDashboardCard(
                    title = "Total Leaves",
                    count = leaveCount,
                    backgroundColor = Color(0xFF6200EE),
                    icon = Icons.Default.EventNote,
                    onClick = { navController.navigate(ROUTE_MYLEAVES) }


                )
            }
            item {
                EmployeeDashboardCard(
                    title = "Pending Leaves",
                    count = pendingLeaveCount,
                    backgroundColor = Color(0xFFDFAC61),
                    icon = Icons.Default.HourglassEmpty,
                    onClick = { navController.navigate(ROUTE_MYLEAVES) }

                )
            }
            item {
                EmployeeDashboardCard(
                    title = "Approved Leaves",
                    count = approvedLeaveCount,
                    backgroundColor = Color(0xFF018786),
                    icon = Icons.Default.CheckCircle,
                    onClick = { navController.navigate(ROUTE_MYLEAVES) }
                )
            }
            item {
                EmployeeDashboardCard(
                    title = "Rejected Leaves",
                    count = rejectedLeaveCount,
                    backgroundColor = Color(0xFFE63D3D),
                    icon = Icons.Default.Close,
                    onClick = { navController.navigate(ROUTE_MYLEAVES) }

                )
            }



        }
    }
}




@Composable
fun EmployeeDashboardCard(
    title: String,
    onClick: () -> Unit,
    count: Comparable<*>,
    backgroundColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = 10.dp,
        backgroundColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun EmployeeBottomBar(navController: NavHostController){
    BottomAppBar(
        contentColor = Color.White,
        containerColor = Color(0xff6f2dc2),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .height(48.dp)
    ) {


        IconButton(
            onClick = { navController.navigate(ROUTE_EMPLOYEEDASHBOARD) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = "Dashboard",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "DASHBOARD",
                    fontWeight = FontWeight.Black,
                    color= Color.White,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(90.dp) // Adjusted width for fitting
                )
            }
        }

        IconButton(
            onClick = { navController.navigate(ROUTE_APPLY) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "ApplyLeave",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "APPLY LEAVE",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(90.dp) // Adjusted width for fitting
                )
            }
        }

        IconButton(
            onClick = { navController.navigate(ROUTE_MYLEAVES) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "leaves",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "MY LEAVES",
                    fontWeight = FontWeight.Black,
                    color= Color.White,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(90.dp) // Adjusted width for fitting
                )
            }
        }


        IconButton(
            onClick = { navController.navigate(ROUTE_EMPLOYEESETTINGS) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "SETTINGS",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(90.dp) // Adjusted width for fitting
                )
            }
        }
    }
}



suspend fun getPendingLeaveCount(email: String): Int {
    val db = FirebaseFirestore.getInstance()
    val querySnapshot = db.collection("leave_applications")
        .whereEqualTo("leaveStatus", "pending")
        .whereEqualTo("email", email)
        .get()
        .await()

    return querySnapshot.size()
}

suspend fun getApprovedLeaveCount(email: String): Int {
    val db = FirebaseFirestore.getInstance()
    val querySnapshot = db.collection("leave_applications")
        .whereEqualTo("leaveStatus", "approved")
        .whereEqualTo("email", email)
        .get()
        .await()

    return querySnapshot.size()
}

suspend fun getRejectedLeaveCount(email: String): Int {
    val db = FirebaseFirestore.getInstance()
    val querySnapshot = db.collection("leave_applications")
        .whereEqualTo("leaveStatus", "rejected")
        .whereEqualTo("email", email)
        .get()
        .await()

    return querySnapshot.size()
}

suspend fun getLeaveCount(email: String): Int {
    val db = FirebaseFirestore.getInstance()
    val querySnapshot = db.collection("leave_applications")
        .whereEqualTo("email", email)
        .get()
        .await()

    return querySnapshot.size()
}