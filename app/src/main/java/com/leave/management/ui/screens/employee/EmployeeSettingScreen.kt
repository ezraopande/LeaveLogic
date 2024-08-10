package com.leave.management.ui.screens.employee

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_APPLY
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.navigation.ROUTE_EMPLOYEEACCOUNT
import com.leave.management.navigation.ROUTE_EMPLOYEESETTINGS
import com.leave.management.navigation.ROUTE_MYLEAVES
import com.leave.management.navigation.ROUTE_LOGIN



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSettingScreen(navController: NavHostController) {

    val mContext = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showTermsPrivacyDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

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
                    // Handle errors
                }
        }
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        Dialog(onDismissRequest = { showPasswordDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
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
                        Button(
                            onClick = { showPasswordDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
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
                                                showPasswordDialog = false
                                                passwordError = ""
                                                Toast.makeText(context, "Password reset successful", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { error ->
                                                passwordError = error
                                            }
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff6f2dc2), contentColor = Color.White)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout), // Replace with your icon
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    if (showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutDialog = false },
                            title = { Text(text = "Confirm") },
                            text = { Text("Are you sure you want to log out?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showLogoutDialog = false
                                        userlogout(context, navController)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff6f2dc2), contentColor = Color.White)
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showLogoutDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("No")
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color(0xff6f2dc2)),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .height(56.dp)
            )
        },
        bottomBar = { EmployeeBottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { navController.navigate(ROUTE_EMPLOYEEACCOUNT) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "view profile")
                }
            }

            // Terms and Privacy Policy Card
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { showTermsPrivacyDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Terms and Privacy Policy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Read Terms and Privacy Policy",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "terms & privacy")
                }
            }

            // Extra Card
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Extra",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Rate Me", fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { /* Handle rate me click */ }) {
                            Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = "rate me")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Feedback", fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            val feedbackIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/email"
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("your@email.com"))
                                putExtra(Intent.EXTRA_SUBJECT, "Feedback on App")
                                putExtra(Intent.EXTRA_TEXT, "Dear Developer,\n\nI would like to share the following feedback:\n\n")
                            }
                            mContext.startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"))
                        }) {
                            Icon(imageVector = Icons.Filled.Quiz, contentDescription = "feedback")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Share App", fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out this awesome app!")
                            }
                            mContext.startActivity(Intent.createChooser(shareIntent, "Share"))
                        }) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = "share app")
                        }
                    }
                }
            }

            // About App Developer Card
            Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About App Developer",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Developed by", color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Salma Sirat", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Contact", color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "0721793739", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun updatePassword(
    currentPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: return onFailure("User not logged in")

    val credential = EmailAuthProvider.getCredential(email, currentPassword)

    user.reauthenticate(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure("Failed to update password")
                        }
                    }
            } else {
                onFailure("Current password do not match")
            }
        }
}

@Preview(showBackground = true)
@Composable
fun EmployeeSettingScreenPreview(){
    EmployeeSettingScreen(navController = rememberNavController())

}