package com.leave.management.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.R
import com.leave.management.navigation.ROUTE_ADMINLOGIN
import com.leave.management.navigation.ROUTE_LOGIN
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val mContext = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mobilenumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Create Admin Account",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(Color.Cyan),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars),
            navigationIcon = {
                IconButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "arrowback")
                }
            }
        )
        Spacer(modifier = Modifier.height(130.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.createacct),
                contentDescription = "",
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                .shadow(5.dp, shape = CutCornerShape(5.dp))
                .width(400.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 50.dp, end = 50.dp, top = 20.dp, bottom = 20.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Full Name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "personIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    )
                )

                TextField(
                    value = mobilenumber,
                    onValueChange = { mobilenumber = it },
                    label = { Text(text = "Mobile Number") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Call, contentDescription = "callIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "emailIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = "passwordIcon")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Cyan,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    scope.launch {
                        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && mobilenumber.isNotEmpty()) {
                            isLoading = true
                            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val signInMethods = task.result.signInMethods
                                    if (signInMethods.isNullOrEmpty()) {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { createTask ->
                                                if (createTask.isSuccessful) {
                                                    val user = auth.currentUser
                                                    val userId = user?.uid
                                                    val userMap = hashMapOf(
                                                        "name" to name,
                                                        "email" to email,
                                                        "mobileNumber" to mobilenumber
                                                    )
                                                    userId?.let {
                                                        firestore.collection("users").document(it)
                                                            .set(userMap)
                                                            .addOnSuccessListener {
                                                                navController.navigate(ROUTE_LOGIN)
                                                            }
                                                            .addOnFailureListener { e ->
                                                                isLoading = false
                                                                Toast.makeText(mContext, "Failed to store user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                                            }
                                                    }
                                                } else {
                                                    isLoading = false
                                                    Toast.makeText(mContext, "Registration failed: ${createTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(mContext, "Email is already registered", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(mContext, "Failed to check email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .size(width = 150.dp, height = 50.dp)
                    .padding(start = 20.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(Color.Cyan)
            ) {
                Text(
                    text = "Register",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Text(
                text = "Already have an account?",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Login",
                color = Color.Blue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(ROUTE_ADMINLOGIN) }
            )
        }
    }
}
