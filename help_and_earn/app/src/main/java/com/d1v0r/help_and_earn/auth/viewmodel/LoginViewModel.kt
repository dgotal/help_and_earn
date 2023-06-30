package com.d1v0r.help_and_earn.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _loginSuccess: MutableStateFlow<String?> = MutableStateFlow(null)
    val loginSuccess: StateFlow<String?> = _loginSuccess.asStateFlow()
    fun login(email: String, password: String, onLoginSuccess: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        signInWithEmail(auth, email, password, onLoginSuccess)
    }

    private fun signInWithEmail(
        auth: FirebaseAuth,
        email: String,
        password: String,
        onLoginSuccess: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserRole(auth.currentUser?.uid ?: "", onLoginSuccess)
                    //_loginSuccess.value = "Success"
                } else {
                    _loginSuccess.value = "Failed"
                }
            }
            .addOnFailureListener {
                _loginSuccess.value = "Failed"
            }
    }

    private fun fetchUserRole(
        uid: String,
        onLoginSuccess: (String) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                if (!role.isNullOrEmpty()) {
                    onLoginSuccess(role)
                } else {
                    _loginSuccess.value = "Failed"
                    Log.d("test", "Role error")
                }
            }
            .addOnFailureListener {
                _loginSuccess.value = "Failed"
                Log.d("test", "Role error")
            }
    }
}