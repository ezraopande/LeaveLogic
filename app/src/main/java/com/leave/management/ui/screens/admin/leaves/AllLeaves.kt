package com.leave.management.ui.screens.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.ui.screens.admin.leaves.ApprovedLeaveScreen
import com.leave.management.ui.screens.admin.leaves.PendingLeaveScreen
import com.leave.management.ui.screens.admin.leaves.RejectedLeaveScreen
import com.leave.management.ui.screens.admin.leaves.ShowApproveDialog
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AllLeaves(navController: NavHostController) {
    val tabs = listOf("PENDING", "APPROVED", "REJECTED")
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

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

    Scaffold(
        bottomBar = {
            AdminNavBar(navController = navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> PendingLeaveScreen(navController, userName)
                    1 -> ApprovedLeaveScreen(navController)
                    2 -> RejectedLeaveScreen(navController)
                }
            }
        }
    }
}

@Composable
fun LeaveItem(leave: LeaveApplication, onApprove: (String) -> Unit, onReject: (String) -> Unit, userName: String) {
//    var showApproveDialog by remember { mutableStateOf(false) }
//    var comment by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = leave.proofUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Leave Type: ${leave.leaveType}",
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "By: ${leave.applied_by}, ${leave.email}",
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "From Date: ${leave.fromDate}", style = MaterialTheme.typography.body1)
                Text(text = "To Date: ${leave.toDate}", style = MaterialTheme.typography.body1)
                Text(text = "Number of Days: ${leave.numberOfDays}", style = MaterialTheme.typography.body1)
                Text(text = "Reason: ${leave.reason}", style = MaterialTheme.typography.body1)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApprove(leave.id) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Approve",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Approve", color = Color.White)
                    }

                    Button(
                        onClick = { onReject(leave.id) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reject",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Reject", color = Color.White)
                    }
                }


            }
        }
    }
}

data class LeaveApplication(
    val id: String = "",
    val leaveType: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val numberOfDays: Int = 0,
    val reason: String = "",
    val proofUrl: String = "",
    val leaveStatus: String = "",
    val applied_by: String = "",
    val comment: String = "",
    val handled_by: String = "",
    val email: String = ""
)

