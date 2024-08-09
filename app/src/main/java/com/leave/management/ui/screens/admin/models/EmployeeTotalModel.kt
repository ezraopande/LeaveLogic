package com.leave.management.ui.screens.admin.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeTotalModel : ViewModel() {
    private val _totalEntries = MutableLiveData<Int>()
    val totalEntries: LiveData<Int> = _totalEntries

    init {
        fetchTotalEntries()
    }

    private fun fetchTotalEntries() {
        val db = FirebaseFirestore.getInstance()
        db.collection("employees").get()
            .addOnSuccessListener { result ->
                _totalEntries.value = result.size()
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }
}
