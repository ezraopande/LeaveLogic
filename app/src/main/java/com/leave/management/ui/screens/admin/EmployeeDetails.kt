package com.leave.management.ui.screens.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EmployeeDetailsScreen(email: String, navController: NavController) {
    var employee by remember { mutableStateOf<Employees?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val mContext = LocalContext.current
    val Context = LocalContext.current


    LaunchedEffect(email) {
        try {
            employee = fetchEmployeeByEmail(email)
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            isError = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Employee Details",
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                        },
                navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                },
                actions = {
                    IconButton(onClick = { showShareDialog = true}) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Details with Employee", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color(0xff6f2dc2)),
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
            )
        },
        bottomBar = { AdminNavBar(navController = navController) }
    ) {
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            isError -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load employee details")
            }
            employee != null -> {
                val emp = employee!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(emp.image_url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color(0xff6f2dc2), CircleShape)
                                    .background(Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = emp.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = emp.designation,
                                fontStyle = FontStyle.Italic,
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextWithIcon(
                                label = "Email",
                                value = emp.email,
                                icon = Icons.Default.Email
                            )
                            TextWithIcon(
                                label = "Mobile",
                                value = emp.mobilenumber,
                                icon = Icons.Default.Phone
                            )
                            TextWithIcon(
                                label = "Address",
                                value = emp.address,
                                icon = Icons.Default.LocationOn
                            )
                            TextWithIcon(
                                label = "Date of Birth",
                                value = emp.dob,
                                icon = Icons.Default.CalendarToday
                            )
                        }
                    }


                    if (showShareDialog) {
                        AlertDialog(
                            onDismissRequest = { showShareDialog = false },
                            modifier = Modifier
                                .width(360.dp)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White, shape = RoundedCornerShape(12.dp)),
                            title = {
                                Text(
                                    text = "Share Employee Details",
                                    style = MaterialTheme.typography.h6,
                                    color = Color.Magenta
                                )
                            },
                            text = {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(buildAnnotatedString {
                                        append("Share the login credentials with ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(emp.name.uppercase())
                                        }
                                    }, style = MaterialTheme.typography.body1)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "You can share the details via Email or SMS.",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.Magenta
                                    )
                                }
                            },
                            confirmButton = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "VIA EMAIL",
                                        color = Color.Magenta,
                                        style = MaterialTheme.typography.button,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_EMAIL, arrayOf(emp.email))
                                                putExtra(Intent.EXTRA_SUBJECT, "Welcome to the Company. You can now apply for leaves.")
                                                putExtra(Intent.EXTRA_TEXT, "Hello ${emp.name},\n\nWelcome to the company. Here are your login credentials.\n\nEmail: ${emp.email}\nPassword: ${emp.password}")
                                            }
                                            mContext.startActivity(shareIntent)
                                            showShareDialog = false
                                        }
                                    )
                                    Text(
                                        text = "VIA SMS",
                                        color = Color.Magenta,
                                        style = MaterialTheme.typography.button,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("smsto:${emp.mobilenumber}")
                                                putExtra("sms_body", "Hello ${emp.name}, these are your credentials: Email: ${emp.email}, Password: ${emp.password}")
                                            }
                                            mContext.startActivity(smsIntent)
                                            showShareDialog = false
                                        }
                                    )
                                    Text(
                                        text = "CANCEL",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.button,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            showShareDialog = false
                                        }
                                    )
                                }

                            },
                            dismissButton = {}
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun TextWithIcon(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$label icon",
            tint = Color(0xff6f2dc2),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

suspend fun fetchEmployeeByEmail(email: String, db: FirebaseFirestore = FirebaseFirestore.getInstance()): Employees? {
    val querySnapshot = db.collection("employees")
        .whereEqualTo("email", email)
        .get()
        .await()

    return if (!querySnapshot.isEmpty) {
        querySnapshot.documents[0].toObject<Employees>()
    } else {
        null
    }
}
