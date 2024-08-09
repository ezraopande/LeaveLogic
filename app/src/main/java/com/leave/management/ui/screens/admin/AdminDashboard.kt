package com.leave.management.ui.screens.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_ADDEMPLOYEE
import com.leave.management.navigation.ROUTE_ADMINACCOUNT
import com.leave.management.navigation.ROUTE_ADMINDASBOARD
import com.leave.management.navigation.ROUTE_ADMINSETTINGS
import com.leave.management.navigation.ROUTE_VIEWLEAVEs
import com.leave.management.navigation.ROUTE_VIEWEMPLOYEES
import com.leave.management.ui.screens.admin.models.EmployeeTotalModel
import com.leave.management.ui.screens.employee.getGreeting
import kotlinx.coroutines.tasks.await
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavHostController) {




    Scaffold(
        bottomBar = {  AdminNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            GreetingSection()
            DashboardScreen(navController)



        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GreetingSection() {
    val greet = getGreeting()
    val mContext = LocalContext.current
    val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("loggedInUserEmail", "") ?: ""

    var userName by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userName = document.getString("name") ?: ""
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
                text = if (userName.isNotEmpty()) "Hi, $userName" else "Loading...",
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
        Image(
            painter = painterResource(id = R.drawable.leaveicon2),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xffF3F9F8))
        )
    }
}





@Composable
fun DashboardScreen(navController: NavHostController,viewModel: EmployeeTotalModel = viewModel()) {
    val totalEmployees by viewModel.totalEntries.observeAsState()
    var rejectedCount by remember { mutableStateOf(0) }
    var leaveCount by remember { mutableStateOf(0) }
    var approvedCount by remember { mutableStateOf(0) }
    var pendingCount by remember { mutableStateOf(0) }

    val db = Firebase.firestore

    LaunchedEffect(Unit) {
        db.collection("leave_applications")
            .get()
            .addOnSuccessListener { result ->
                leaveCount = result.size()
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }



        try {
            val querySnapshot: QuerySnapshot = db.collection("leave_applications")
                .whereEqualTo("leaveStatus", "rejected")
                .get()
                .await()
            approvedCount = querySnapshot.size()
        } catch (e: Exception) {
            // Handle the error
            e.printStackTrace()
        }

        try {
            val querySnapshot: QuerySnapshot = db.collection("leave_applications")
                .whereEqualTo("leaveStatus", "approved")
                .get()
                .await()
            rejectedCount = querySnapshot.size()
        } catch (e: Exception) {
            // Handle the error
            e.printStackTrace()
        }

        try {
            val querySnapshot: QuerySnapshot = db.collection("leave_applications")
                .whereEqualTo("leaveStatus", "pending")
                .get()
                .await()
            pendingCount = querySnapshot.size()
        } catch (e: Exception) {
            // Handle the error
            e.printStackTrace()
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
                DashboardCard(
                    title = "Total Employees",
                    count = totalEmployees ?: "",
                    backgroundColor = Color(0xFFBB86FC),
                    icon = Icons.Default.People,
                    onClick = { navController.navigate(ROUTE_VIEWEMPLOYEES) }
                )
            }
            item {
                DashboardCard(
                    title = "Total Leaves",
                    count = leaveCount,
                    backgroundColor = Color(0xFF6200EE),
                    icon = Icons.Default.EventNote,
                    onClick = { navController.navigate(ROUTE_VIEWLEAVEs)  }
                )
            }
            item {
                DashboardCard(
                    title = "Pending Leaves",
                    count = pendingCount,
                    backgroundColor = Color(0xFFDFAC61),
                    icon = Icons.Default.HourglassEmpty,
                    onClick = { navController.navigate(ROUTE_VIEWLEAVEs) }
                )
            }
            item {
                DashboardCard(
                    title = "Approved Leaves",
                    count = rejectedCount,
                    backgroundColor = Color(0xFF346F37),
                    icon = Icons.Default.CheckCircle,
                    onClick = { navController.navigate(ROUTE_VIEWLEAVEs) }
                )
            }

            item {
                DashboardCard(
                    title = "Rejected Leaves",
                    count = approvedCount,
                    backgroundColor = Color(0xFFE63D3D),
                    icon = Icons.Default.Close,
                    onClick = { navController.navigate(ROUTE_VIEWLEAVEs) }

                )
            }



        }
    }
}




@Composable
fun DashboardCard(
    title: String,
    count: Comparable<*>,
    backgroundColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .clickable { onClick() }
            .fillMaxWidth(),
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
fun AdminNavBar(navController: NavController){

    androidx.compose.material.BottomAppBar(
        contentColor = Color.White,
        backgroundColor = Color(0xff6f2dc2),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        androidx.compose.material.IconButton(
            onClick = { navController.navigate(ROUTE_ADMINDASBOARD) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = "dashboard",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.material.Text(
                    text = "DASHBOARD",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }



        androidx.compose.material.IconButton(
            onClick = { navController.navigate(ROUTE_VIEWLEAVEs) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "Leaves",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.material.Text(
                    text = "LEAVES",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }

        androidx.compose.material.IconButton(
            onClick = { navController.navigate(ROUTE_ADDEMPLOYEE) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = "AddEmployee",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.material.Text(
                    text = "ADD EMPLOYEE",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }

        androidx.compose.material.IconButton(
            onClick = { navController.navigate(ROUTE_VIEWEMPLOYEES) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = "Employees",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.material.Text(
                    text = "EMPLOYEES",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }

        androidx.compose.material.IconButton(
            onClick = { navController.navigate(ROUTE_ADMINACCOUNT) },
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.material.Text(
                    text = "ACCOUNT",
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }
        }
    }
}