package com.leave.management.ui.screens.employee




import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.ui.screens.admin.logout


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAccount(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("user_email", "") ?: ""

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userMobile by remember { mutableStateOf("") }
    var userDesignation by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }
    var userDob by remember { mutableStateOf("") }
    var userImage by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            db.collection("employees")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userName = document.getString("name") ?: ""
                        userEmail = document.getString("email") ?: ""
                        userMobile = document.getString("mobilenumber") ?: ""
                        userDesignation = document.getString("designation") ?: ""
                        userAddress = document.getString("address") ?: ""
                        userDob = document.getString("dob") ?: ""
                        userImage = document.getString("image_url") ?: ""
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            passwordError = ""
                            when {
                                currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                                    passwordError = "All fields are required"
                                }
                                newPassword != confirmPassword -> {
                                    passwordError = "Passwords do not match"
                                }
                                else -> {
                                    updatePassword(
                                        currentPassword,
                                        newPassword,
                                        onSuccess = {
                                            showDialog = false
                                            passwordError = ""
                                            Toast.makeText(context, "Password reset successful", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailure = { error ->
                                            passwordError = error
                                        }
                                    )
                                }
                            }
                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    text = "Account",
                    color = Color.White,
                    fontSize = 20.sp

                ) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.navigate(
                        ROUTE_EMPLOYEEDASHBOARD
                    )}) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.home),

                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = {
                        logout(
                            context,
                            navController
                        )
                    }) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                colors = TopAppBarDefaults.topAppBarColors(Color(0xFF8d58f0))
            )
        },
        bottomBar = { EmployeeBottomBar(navController = navController) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {


                    Spacer(modifier = Modifier.height(60.dp))


                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color(0xff6f2dc2), CircleShape)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = userImage), // Replace with your image URL
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(100.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = userName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = userDesignation,
                                fontStyle = FontStyle.Italic,
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
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
                        com.leave.management.ui.screens.admin.TextWithIcon(
                            label = "Email",
                            value = userEmail,
                            icon = Icons.Default.Email
                        )
                        com.leave.management.ui.screens.admin.TextWithIcon(
                            label = "Mobile",
                            value = userMobile,
                            icon = Icons.Default.Phone
                        )
                        com.leave.management.ui.screens.admin.TextWithIcon(
                            label = "Address",
                            value = userAddress,
                            icon = Icons.Default.LocationOn
                        )
                        com.leave.management.ui.screens.admin.TextWithIcon(
                            label = "Date of Birth",
                            value = userDob,
                            icon = Icons.Default.CalendarToday
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { showDialog = true }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Change Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF333333)
                        )

                        androidx.compose.material3.Text(
                            text = "Click here to change password",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
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
