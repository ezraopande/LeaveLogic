package com.leave.management.ui.screens.employee

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leave.management.R
import com.leave.management.navigation.ROUTE_ADMINLOGIN
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.navigation.ROUTE_LOGIN
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth: FirebaseAuth = Firebase.auth

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val clicked = remember { mutableStateOf(false) }

    // Update the clicked state when the button is clicked
    val updatedClicked = rememberUpdatedState(clicked.value)

    LaunchedEffect(Unit) {
        val loggedInUserEmail = sharedPreferences.getString("user_email", null)
        if (loggedInUserEmail != null) {
            navController.navigate(ROUTE_EMPLOYEEDASHBOARD) {
                popUpTo(ROUTE_LOGIN) { inclusive = true }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.employeelogin),
                contentDescription = "",
                modifier = Modifier.size(130.dp)
            )
        }

        //Admin login button
        Row {
            Button(
                onClick = { navController.navigate(ROUTE_ADMINLOGIN)
                    // Toggle the clicked state when the button is clicked
                    clicked.value = !updatedClicked.value},
                modifier = Modifier
                    .size(width = 170.dp, height = 50.dp)
                    .clickable { clicked.value = !clicked.value } // Toggle clicked state when clicked
//                    .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(start = 20.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    Color.Transparent, // Set button background color
                    contentColor = Color.Black // Set button text color
                ),
                border = BorderStroke(3.dp, Color.Black)
            ) {

                Text(
                    text = "Admin",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(2.dp))

            //Employee login Button
            Button(
                onClick = { navController.navigate(ROUTE_LOGIN)
                    // Toggle the clicked state when the button is clicked
                    clicked.value = !updatedClicked.value },
                modifier = Modifier
                    .size(width = 170.dp, height = 50.dp)
                    .clickable { clicked.value = !clicked.value } // Toggle clicked state when clicked
//                    .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                    .padding(start = 20.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    Color.Cyan, // Set button background color
                    contentColor = Color.Black // Set button text color
                ),
                border = BorderStroke(3.dp, Color.Black)
            ) {
                Text(
                    text = "Employee",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))


        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .shadow(5.dp, shape = CutCornerShape(5.dp))
                .width(400.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Welcome! Please log in to proceed",
                modifier = Modifier.padding(start = 30.dp, bottom = 10.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 50.dp, end = 50.dp, top = 20.dp, bottom = 20.dp)
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "emailIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "passwordIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                },
                modifier = Modifier
                    .size(width = 150.dp, height = 50.dp)
                    .padding(start = 20.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(Color.Cyan)
            ) {
                Text(
                    text = "Login",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LaunchedEffect(isLoading) {
            if (isLoading) {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()

                    // Save email to SharedPreferences
                    with(sharedPreferences.edit()) {
                        putString("user_email", email)
                        apply()
                    }

                    navController.navigate(ROUTE_EMPLOYEEDASHBOARD)
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Login failed"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        }
    }
}

// Function to retrieve email from SharedPreferences
fun getEmailFromPreferences(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("user_email", null)
}


fun userlogout(context: Context, navController: NavHostController) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove("user_email")
        apply()
    }
    navController.navigate(ROUTE_LOGIN) {
        popUpTo(ROUTE_EMPLOYEEDASHBOARD) { inclusive = true }
    }
}