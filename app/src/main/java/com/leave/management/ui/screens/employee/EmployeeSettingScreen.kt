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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun EmployeeSettingScreen(navController: NavHostController){

    var presses by remember { mutableIntStateOf(0) }
    val mContext = LocalContext.current
    var search by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) } // State to control dialog visibility
    var showTermsPrivacyDialog by remember { mutableStateOf(false) } // State to control dialog visibility
    var showPasswordDialog by remember { mutableStateOf(false) } // State to control dialog visibility




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

//    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        Dialog(onDismissRequest = { showPasswordDialog = false }) {
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
                        Button(colors = ButtonDefaults.buttonColors(Color.Red),
                            onClick = { showPasswordDialog = false }) {
                            Text("Cancel", color = Color.White)
                        }
                        Button(colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff6f2dc2),
                            contentColor = MaterialTheme.colorScheme.onPrimary),
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
                        }) {
                            Text("Confirm", color = Color.White)
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
                    Column {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(top=10.dp)

                        )
                    }

                },actions = {
                    IconButton(onClick = {showLogoutDialog=true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout), // Replace with your icon
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    //confirmation dialogue
                    if (showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutDialog = false },
                            title = { Text(text = "Confirm") },
                            text = { Text("Are you sure you want to log out?") },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xff6f2dc2),
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ), onClick = {
                                        showLogoutDialog=false
                                        userlogout(context, navController)

                                        // Handle logout logic here
                                    }
                                ) {
                                    Text("Yes", color = Color.White)
                                }
                            },
                            dismissButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(Color.Red),
                                    onClick = { showLogoutDialog = false }
                                ) {
                                    Text("No")
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(Color(0xff6f2dc2)),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .height(48.dp)
            )
        },
        bottomBar = { EmployeeBottomBar(navController = navController) }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //Loggedin Employee Profile


            Card (colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(width = 400.dp, height = 40.dp)
            ) {
                Row(modifier = Modifier
                    .clickable { navController.navigate(ROUTE_EMPLOYEEACCOUNT) }
                ) {
                    Text(
                        text = "View Profile",
                        modifier = Modifier
                            .padding(start = 10.dp, top = 10.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default
                    )
                    Spacer(modifier = Modifier.width(220.dp))

                    IconButton(onClick = { navController.navigate(ROUTE_EMPLOYEEACCOUNT) }) { Icon(imageVector = Icons.Filled.Person, contentDescription = "changepassword")
                    }

                }

                Row(modifier = Modifier
                    .clickable { showPasswordDialog=true }
                ) {
                    Text(
                        text = "Change Password",
                        modifier = Modifier
                            .padding(start = 10.dp, top = 10.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default
                    )
                    Spacer(modifier = Modifier.width(220.dp))

                    IconButton(onClick = {showPasswordDialog=true}) { Icon(imageVector = Icons.Filled.ArrowForwardIos, contentDescription = "changepassword")
                    }

                }
            }

            Card (colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(width = 400.dp, height = 50.dp)
            ) {
                Row {
                    Column {
                        Text(
                            text = "Terms and Privacy Policy",
                            modifier = Modifier.padding(start = 5.dp, top = 5.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default
                        )
                        Text(
                            text = "Read Terms and Privacy Policy",
                            modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(160.dp))
                    IconButton(onClick = {showTermsPrivacyDialog=true}) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = "terms&conditions")

                    }

                    if (showTermsPrivacyDialog) {
                        AlertDialog(
                            onDismissRequest = { showTermsPrivacyDialog = false },
                            title = { Text(text = "Terms and Privacy Policy") },
                            text = {
                                Column {
                                    Text("1. Leave application stores application data on Firebase server.")
                                    Divider(
                                        color = Color.Gray,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text("2. We are not responsible of loss of data in case of any isssues.")
                                    Divider(
                                        color = Color.Gray,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Text("3. Keep the leave app up to date to enjoy new features.")
                                    // Add more terms and conditions as needed
                                }
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xff6f2dc2),
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    onClick = { showTermsPrivacyDialog = false }
                                ) {
                                    Text("OK", color=Color.White)
                                }
                            },
                        )
                    }

                }
            }
            Card (colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(width = 400.dp, height = 200.dp)
            ){
                Text(text = "Extra",
                    modifier = Modifier.
                    padding(start = 5.dp, top = 10.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Text(
                        text = "Rate Us",
                        modifier = Modifier.
                        padding(start = 5.dp, top = 5.dp),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(300.dp))
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = "like")

                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(text = "Feedback", modifier = Modifier.
                    padding(start = 5.dp, top= 5.dp) ,
                        fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(280.dp))
                    IconButton(onClick = {val feedbackIntent = Intent(Intent.ACTION_SEND)
                        feedbackIntent.type = "text/email" // You can use "message/rfc822" for email MIME type
                        feedbackIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("your@email.com"))
                        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Salma's Leave app")
                        feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Dear Developer,\n\nI would like to share the following feedback:\n\n")
                        mContext.startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"))}) {
                        Icon(imageVector = Icons.Filled.Quiz, contentDescription = "feedback")

                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(text = "Share App", modifier = Modifier.
                    padding(start = 5.dp, top= 5.dp) ,
                        fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(275.dp))
                    IconButton(onClick = {val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "CHECK OUT THIS IS MY LEAVE APP")
                        mContext.startActivity(Intent.createChooser(shareIntent, "Share")) }) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "shareapp")

                    }
                }
            }
            Card (colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(width = 400.dp, height = 120.dp)
            ){
                Text(text = "About App Developer",
                    modifier = Modifier.
                    padding(start = 5.dp, top = 10.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    color = Color.Blue
                )
                Row {
                    Text(text = "Developed by", modifier = Modifier.padding(start = 5.dp, top= 5.dp) , color = Color.Gray)
                    Spacer(modifier = Modifier.width(160.dp))
                    Text(text = "Salma Sirat", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,modifier = Modifier.padding(start = 5.dp, top= 5.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Text(text = "Contact", modifier = Modifier.padding(start = 5.dp, top= 5.dp) , color = Color.Gray)
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "0721793739", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,modifier = Modifier.padding(start = 5.dp, top= 5.dp))
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