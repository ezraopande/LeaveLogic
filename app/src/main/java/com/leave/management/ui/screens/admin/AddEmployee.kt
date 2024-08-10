package com.leave.management.ui.screens.admin

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.leave.management.navigation.ROUTE_ADDEMPLOYEE
import com.leave.management.navigation.ROUTE_ADMINDASBOARD
import com.leave.management.navigation.ROUTE_ADMINSETTINGS
import com.leave.management.navigation.ROUTE_VIEWLEAVEs
import com.leave.management.navigation.ROUTE_VIEWEMPLOYEES
import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(navController: NavHostController) {
    val mContext = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var mobilenumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? -> imageUri = uri }
    var showSaveEmployeeDialog by remember { mutableStateOf(false) }
    var showEmailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    if (showDatePicker) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                showDatePicker = false
            },
            year, month, day
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Register Employee",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.White),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .shadow(4.dp)
            )
        },
        bottomBar = {
            AdminNavBar(navController = navController)
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xff6f2dc2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        imageUri?.let {
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap.value = MediaStore.Images.Media.getBitmap(
                                    context.contentResolver, it
                                )
                            } else {
                                val source = ImageDecoder.createSource(context.contentResolver, it)
                                bitmap.value = ImageDecoder.decodeBitmap(source)
                            }

                            bitmap.value?.let { btm ->
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                )
                            }
                        } ?: Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Default Image",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff6f2dc2),
                            contentColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(50))
                            .height(48.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Select Image",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    isError = name.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        showEmailError = false
                    },
                    label = { Text("Email") },
                    isError = showEmailError || email.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    isError = password.isEmpty(),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = mobilenumber,
                    onValueChange = { mobilenumber = it },
                    label = { Text("Mobile Number") },
                    isError = mobilenumber.isEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    isError = address.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth") },
                    isError = dob.isEmpty(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation") },
                    isError = designation.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            item {
                if (isLoading) {
                    Box( modifier = Modifier
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp),
                            color = MaterialTheme.colorScheme.primary

                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mobilenumber.isEmpty() || address.isEmpty() || dob.isEmpty() || designation.isEmpty()) {
                                Toast.makeText(mContext, "All fields are required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            val employee = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "password" to password,
                                "mobilenumber" to mobilenumber,
                                "address" to address,
                                "dob" to dob,
                                "designation" to designation
                            )

                            db.collection("employees")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { authTask ->
                                                if (authTask.isSuccessful) {
                                                    db.collection("employees")
                                                        .add(employee)
                                                        .addOnSuccessListener { documentReference ->
                                                            imageUri?.let { uri ->
                                                                val storageRef = storage.reference
                                                                val imageRef = storageRef.child("profile_images/${documentReference.id}")
                                                                val uploadTask = imageRef.putFile(uri)
                                                                uploadTask.addOnSuccessListener {
                                                                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                                                        documentReference.update("image_url", downloadUri.toString())
                                                                            .addOnSuccessListener {

                                                                                Toast.makeText(mContext, "Successfully", Toast.LENGTH_SHORT).show()
                                                                                navController.navigate(ROUTE_VIEWEMPLOYEES)
                                                                                showSaveEmployeeDialog = true
                                                                            }
                                                                    }
                                                                }
                                                            } ?: run {

                                                            }
                                                        }
                                                } else {
                                                    Toast.makeText(mContext, "Failed to register employee", Toast.LENGTH_SHORT).show()
                                                }
                                                isLoading = false
                                            }
                                    } else {
                                        showEmailError = true
                                        Toast.makeText(mContext, "Email already registered", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(mContext, "Failed to check email registration", Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff6f2dc2),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Submit",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

}






@Preview(showBackground = true)
@Composable
fun AddEmployeeScreenPreview() {
    AddEmployeeScreen(navController = rememberNavController())
}
