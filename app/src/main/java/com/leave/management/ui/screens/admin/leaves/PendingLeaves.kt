package com.leave.management.ui.screens.admin.leaves

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.leave.management.navigation.ROUTE_VIEWLEAVEs
import com.leave.management.ui.screens.admin.LeaveApplication
import com.leave.management.ui.screens.admin.LeaveItem
import kotlinx.coroutines.launch


@Composable
fun PendingLeaveScreen(navController: NavHostController, userName: String) {
    var pendingLeaves by remember { mutableStateOf<List<LeaveApplication>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }  // State for search query
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showRejectDialog by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var selectedLeaveId by remember { mutableStateOf("") }

    // Fetch pending leaves
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchPendingLeaves { leaves, error ->
                pendingLeaves = leaves ?: emptyList()
                isLoading = false
                // Handle error if necessary
            }
        }
    }

    // Filtered leaves based on search query
    val filteredLeaves = pendingLeaves.filter { leave ->
        leave.applied_by.contains(searchQuery, ignoreCase = true) ||
                leave.email.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { query -> searchQuery = query },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFF0F0F0),
                    RoundedCornerShape(24.dp)
                ) // Background and corner shape
                .shadow(4.dp, RoundedCornerShape(24.dp)), // Add shadow for elevation
            placeholder = { Text(text = "Search by name or email", color = Color.Gray) },
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
        Spacer(modifier = Modifier.height(15.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (filteredLeaves.isEmpty()) {

                Text(
                    text = "No pending leaves",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredLeaves) { leave ->
                        LeaveItem(
                            leave = leave,
                            onApprove = { leaveId ->
                                selectedLeaveId = leaveId
                                showApproveDialog = true
                            },
                            onReject = { leaveId ->
                                selectedLeaveId = leaveId
                                showRejectDialog = true
                            },
                            userName = userName
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showApproveDialog) {
            ShowApproveDialog(
                context = context,
                leaveId = selectedLeaveId,
                userName = userName,
                onApproveConfirmed = { success ->
                    showApproveDialog = false
                    if (success) {
                        Toast.makeText(context, "Leave Approved", Toast.LENGTH_SHORT).show()
                        fetchPendingLeaves { leaves, _ ->
                            pendingLeaves = leaves ?: emptyList()
                        }
                    } else {
                        Toast.makeText(context, "Failed to approve leave", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = { showApproveDialog = false }
            )
        }

        if (showRejectDialog) {
            ShowRejectDialog(
                context = context,
                leaveId = selectedLeaveId,
                userName = userName,
                onRejectConfirmed = { success ->
                    showRejectDialog = false
                    if (success) {
                        Toast.makeText(context, "Leave rejected", Toast.LENGTH_SHORT).show()
                        fetchPendingLeaves { leaves, _ ->
                            pendingLeaves = leaves ?: emptyList()
                        }
                    } else {
                        Toast.makeText(context, "Failed to reject leave", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = { showRejectDialog = false }
            )
        }
    }
}

private fun fetchPendingLeaves(onResult: (List<LeaveApplication>?, Exception?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("leave_applications")
        .whereEqualTo("leaveStatus", "pending")
        .get()
        .addOnSuccessListener { result ->
            val leaves = result.documents.mapNotNull { document ->
                document.toObject(LeaveApplication::class.java)?.copy(id = document.id)
            }
            onResult(leaves, null)
        }
        .addOnFailureListener { exception ->
            onResult(null, exception)
        }
}

@Composable
fun ShowRejectDialog(
    context: Context,
    leaveId: String,
    userName: String,
    onRejectConfirmed: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    var comment by remember { mutableStateOf("") }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onDismiss()
            },
            title = {
                Text(
                    text = "Reject Leave",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "Please provide a reason for rejection:",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                            cursorColor = MaterialTheme.colors.primary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (comment.isNotEmpty()) {
                            rejectLeave(leaveId, comment, userName) { success ->
                                onRejectConfirmed(success)
                            }
                            openDialog = false
                        } else {
                            Toast.makeText(context, "Comment is required", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        openDialog = false
                        onDismiss()
                    },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                ) {
                    Text("Cancel", color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ShowApproveDialog(
    context: Context,
    leaveId: String,
    userName: String,
    onApproveConfirmed: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    var comment by remember { mutableStateOf("") }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onDismiss()
            },
            title = {
                Text(
                    text = "Approve Leave",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "Please Add a comment:",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                            cursorColor = MaterialTheme.colors.primary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (comment.isNotEmpty()) {
                            approveLeave(leaveId, comment, userName) { success ->
                                onApproveConfirmed(success)
                            }
                            openDialog = false
                        } else {
                            Toast.makeText(context, "Comment is required", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        openDialog = false
                        onDismiss()
                    },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                ) {
                    Text("Cancel", color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}



private fun approveLeave(leaveId: String, comment: String, userName: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("leave_applications")
        .document(leaveId)
        .update(mapOf(
            "leaveStatus" to "approved",
            "comment" to comment,
            "handled_by" to userName
        ))
        .addOnSuccessListener {
            onResult(true)
        }
        .addOnFailureListener {
            onResult(false)
        }
}

private fun rejectLeave(leaveId: String, comment: String, handledBy: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("leave_applications")
        .document(leaveId)
        .update(mapOf(
            "leaveStatus" to "rejected",
            "comment" to comment,
            "handled_by" to handledBy
        ))
        .addOnSuccessListener {
            onResult(true)
        }
        .addOnFailureListener {
            onResult(false)
        }
}