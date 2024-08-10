package com.leave.management.ui.screens.admin

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class Employees(
    val address: String = "",
    val designation: String = "",
    val dob: String = "",
    val email: String = "",
    val mobilenumber: String = "",
    val name: String = "",
    val image_url: String = "",
    val password: String=""
)



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ViewEmployeesScreen(navController: NavController) {
    val employees = remember { mutableStateListOf<Employees>() }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loadEmployees(
            onSuccess = { fetchedEmployees ->
                employees.addAll(fetchedEmployees)
                isLoading = false
            },
            onError = {
                isLoading = false
                isError = true

                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Failed to load employees")
                }
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Registered Employees",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color(0xff6f2dc2)),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)

            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 105.dp)
            ) {

                TextField(
                    value = searchQuery,
                    onValueChange = { query -> searchQuery = query },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp)) // Background and corner shape
                        .shadow(4.dp, RoundedCornerShape(24.dp)), // Add shadow for elevation
                    placeholder = {
                        androidx.compose.material3.Text(
                            text = "Search by name or email",
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray // Tint the search icon
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent, // Transparent background to show custom background
                        focusedIndicatorColor = Color.Transparent, // Remove underline
                        unfocusedIndicatorColor = Color.Transparent // Remove underline
                    )
                )


                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> CircularProgressIndicator()
                        isError -> Text("Failed to load employees")
                        employees.isEmpty() -> Text("No employees found")
                        else -> {
                            val filteredEmployees = employees.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                        it.email.contains(searchQuery, ignoreCase = true) ||
                                        it.mobilenumber.contains(searchQuery)
                            }

                            if (filteredEmployees.isEmpty()) {
                                Text("No employees found")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredEmployees, key = { it.email }) { employee ->
                                        EmployeeItem(
                                            employee = employee,
                                            navController = navController
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AdminNavBar(navController = navController)
        }
    )
}

@Composable
fun EmployeeItem(employee: Employees, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clickable {
                navController.navigate("employeeDetails/${employee.email}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(employee.image_url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xff6f2dc2), CircleShape)
                    .background(MaterialTheme.colors.surface),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employee.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xff333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = employee.email,
                    fontSize = 16.sp,
                    color = Color(0xff666666)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = employee.mobilenumber,
                    fontSize = 16.sp,
                    color = Color(0xff666666)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to details",
                tint = Color(0xff6f2dc2),
                modifier = Modifier.size(24.dp)
            )
        }
    }

}

suspend fun loadEmployees(
    onSuccess: (List<Employees>) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        delay(1000)
        val employees = fetchEmployees()
        onSuccess(employees)
    } catch (e: Exception) {
        onError(e)
    }
}


suspend fun fetchEmployees(db: FirebaseFirestore = FirebaseFirestore.getInstance()): List<Employees> {
    val employeesList = mutableListOf<Employees>()
    val result = db.collection("employees").get().await()
    for (document in result) {
        val employee = document.toObject<Employees>()
        employeesList.add(employee)
    }
    return employeesList
}
