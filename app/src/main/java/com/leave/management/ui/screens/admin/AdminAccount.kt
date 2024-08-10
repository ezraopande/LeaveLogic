package com.leave.management.ui.screens.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_ADMINDASBOARD
import com.leave.management.navigation.ROUTE_ADMINLOGIN
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.ui.screens.employee.EmployeeBottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccount(navController: NavHostController) {
    val mContext = LocalContext.current
    val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("loggedInUserEmail", "") ?: ""

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userMobile by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userName = document.getString("name") ?: ""
                        userEmail = document.getString("email") ?: ""
                        userMobile = document.getString("mobileNumber") ?: ""
                    }
                }
                .addOnFailureListener { exception ->

                }
        }
    }

    fun changePassword() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        if (newPassword == confirmNewPassword) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(mContext, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                        showChangePasswordDialog = false
                                    } else {
                                        errorMessage = updateTask.exception?.message ?: "Password change failed"
                                    }
                                }
                        } else {
                            errorMessage = "Passwords do not match"
                        }
                    } else {
                        errorMessage = "Current password does not match"
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Account", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(ROUTE_ADMINDASBOARD) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),

                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { logout(mContext, navController) }) {
                        Icon(
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
        bottomBar = { AdminNavBar(navController = navController) }
    ) {
      LazyColumn(
          modifier = Modifier
              .fillMaxSize()
      ) {
          item {
              Column(
                  modifier = Modifier
                      .fillMaxSize()
                      .padding(24.dp)
              ) {
                  Spacer(modifier = Modifier.height(100.dp))
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
                                  .data("https://apensoftwares.co.ke/images/media/1669670270logo.png")
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
                              text = userName,
                              fontWeight = FontWeight.Bold,
                              fontSize = 24.sp,
                              color = Color(0xFF333333),
                              modifier = Modifier.padding(bottom = 8.dp)
                          )
                          Text(
                              text = "Administrator",
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
                              value = userEmail,
                              icon = Icons.Default.Email
                          )
                          TextWithIcon(
                              label = "Mobile",
                              value = userMobile,
                              icon = Icons.Default.Phone
                          )
                          TextWithIcon(
                              label = "Password",
                              value = userMobile,
                              icon = Icons.Default.Phone
                          )
                          // Add more fields as needed
                      }
                  }

                  Spacer(modifier = Modifier.height(16.dp))

                  Card(
                      shape = RoundedCornerShape(16.dp),
                      elevation = 8.dp,
                      modifier = Modifier
                          .fillMaxWidth()
                          .clickable { showChangePasswordDialog = true }
                  ) {
                      Column(
                          modifier = Modifier.padding(16.dp),
                          verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                          Text(
                              text = "Change Password",
                              fontWeight = FontWeight.Bold,
                              fontSize = 18.sp,
                              color = Color(0xFF333333)
                          )

                          Text(
                              text = "Click here to change password",
                              fontSize = 14.sp,
                              color = Color(0xFF666666)
                          )
                      }
                  }
              }
          }
      }


        if (showChangePasswordDialog) {
            AlertDialog(
                onDismissRequest = { showChangePasswordDialog = false },
                title = { Text(text = "Change Password") },
                text = {
                    Column {
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
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("Confirm New Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { changePassword() },
                        colors = ButtonDefaults.buttonColors(Color.Green)


                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showChangePasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(Color.Red)


                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AdminProfileStat(label: String, icon: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(text = label, color = Color.White, fontSize = 16.sp)
    }
}

fun logout(mContext: Context, navController: NavHostController) {
    val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove("loggedInUserEmail")
        putBoolean("isLoggedIn", false)
        apply()
    }
    navController.navigate(ROUTE_ADMINLOGIN) {
        popUpTo(ROUTE_ADMINDASBOARD) { inclusive = true }
    }
}
