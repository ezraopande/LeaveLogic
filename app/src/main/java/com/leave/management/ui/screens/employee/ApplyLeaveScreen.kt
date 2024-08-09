package com.leave.management.ui.screens.employee



import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.leave.management.navigation.ROUTE_APPLY
import com.leave.management.navigation.ROUTE_EMPLOYEEDASHBOARD
import com.leave.management.navigation.ROUTE_EMPLOYEEACCOUNT
import com.leave.management.navigation.ROUTE_EMPLOYEESETTINGS
import com.leave.management.navigation.ROUTE_MYLEAVES
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveScreen(navController: NavHostController) {
    val context = LocalContext.current
    val email = getEmailFromPreferences(context) ?: ""
    var selectedOptionText by remember { mutableStateOf("Select leave type") }
    val options = listOf(
        "Select leave type", "Casual Leave", "Sick Leave", "Maternity Leave",
        "Marriage Leave", "Paternity Leave", "Bereavement Leave"
    )
    var expanded by remember { mutableStateOf(false) }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var numberOfDays by remember { mutableStateOf(0) }
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var isSubmitting by remember { mutableStateOf(false) } // Loading state
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            bitmap.value = decodeBitmapFromUri(context, it)
        }
    }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val mContext = LocalContext.current

    var employeeName by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()


    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            db.collection("employees")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        employeeName = document.getString("name") ?: ""
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }


    fun showDatePickerDialog(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            mContext,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formattedDate = dateFormatter.format(selectedDate.time)
                if (isFromDate) {
                    fromDate = formattedDate
                } else {
                    toDate = formattedDate
                }
                if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                    val startDate = dateFormatter.parse(fromDate)!!
                    val endDate = dateFormatter.parse(toDate)!!
                    val diff = endDate.time - startDate.time
                    numberOfDays = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun submitLeaveApplication() {
        if (selectedOptionText == "Select leave type" || fromDate.isEmpty() || toDate.isEmpty() || textValue.text.isEmpty()) {
            // Display error message if any field is empty
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        isSubmitting = true

        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageRef.child("leave_proofs/$fileName")

        val uploadTask = fileRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val leaveData = hashMapOf(
                    "leaveType" to selectedOptionText,
                    "fromDate" to fromDate,
                    "toDate" to toDate,
                    "numberOfDays" to numberOfDays,
                    "reason" to textValue.text,
                    "proofUrl" to downloadUrl.toString(),
                    "leaveStatus" to "pending",
                    "email" to email,
                    "comment" to "",
                    "handled_by" to "",
                    "applied_by" to employeeName
                )

                FirebaseFirestore.getInstance().collection("leave_applications")
                    .add(leaveData)
                    .addOnSuccessListener {
                        // Handle success
                        Toast.makeText(context, "Leave application submitted successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_MYLEAVES)
                        isSubmitting = false
                    }
                    .addOnFailureListener {
                        // Handle failure
                        Toast.makeText(context, "Failed to submit leave application", Toast.LENGTH_SHORT).show()
                        isSubmitting = false
                    }
            }
        }.addOnFailureListener {
            // Handle file upload failure
            Toast.makeText(context, "Failed to upload proof image", Toast.LENGTH_SHORT).show()
            isSubmitting = false
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "New Leave Application",
                            color = Color.White,
                            fontSize = 18.sp,
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(Color(0xff6f2dc2)),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .height(48.dp),
            )
        },
        bottomBar = { EmployeeBottomBar(navController) }
    ) { innerPadding ->


        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ){
            item {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {

                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB993E9),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth() // Make button full width for consistency
                    ) {
                        Text(text = "Pick Proof Image", fontSize = 14.sp)
                    }

                    // Display selected image
                    bitmap.value?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)) // Rounded corners for images
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                        )
                    }

                    // Leave type dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = {},
                            label = { Text("Leave Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                                ),
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOptionText = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // From and To date pickers
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showDatePickerDialog(isFromDate = true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp), // Softer corners for a modern look
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB993E9),
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = "From Date: $fromDate", fontSize = 14.sp)
                        }
                        Button(
                            onClick = { showDatePickerDialog(isFromDate = false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB993E9),
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = "To Date: $toDate", fontSize = 14.sp)
                        }
                    }

                    // Number of days
                    Text(
                        text = "Number of days: $numberOfDays",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Softer color for less emphasis
                            fontSize = 15.sp
                        ),
                    )

                    // Reason input
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        label = { Text("Reason for leave") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                            ),
                    )

                    // Submit button or loading indicator
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp) // Slightly larger for visibility
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Button(
                            onClick = { submitLeaveApplication() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp), // Consistent button height
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff6f2dc2),
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text(text = "Submit", fontSize = 15.sp)
                        }
                    }
                }


            }
        }


    }
}

fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
