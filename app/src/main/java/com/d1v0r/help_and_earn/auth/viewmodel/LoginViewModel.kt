package com.d1v0r.help_and_earn.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.d1v0r.help_and_earn.PreferencesManager
import com.d1v0r.help_and_earn.model.Child
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _showMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val showMessage: StateFlow<String?> = _showMessage.asStateFlow()

    private val _isParentSelected: MutableStateFlow<Boolean> = MutableStateFlow(true) //postavljanje
    val isParentSelected: StateFlow<Boolean> = _isParentSelected.asStateFlow() //citanje

    val isAuthenticated = preferencesManager.getUser() != null
    val isParent = preferencesManager.getIsParent()

    fun onRoleSelected(isSelected: Boolean) {
        _isParentSelected.value = isSelected
    }

    fun signInChild(username: String, passcode: String) {
        firestore.collection("children")
            .whereEqualTo("username", username)
            .whereEqualTo("passcode", passcode)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val uid = querySnapshot.documents.first().toObject(Child::class.java)?.id
                    preferencesManager.saveUser(uid)
                    preferencesManager.setIsParent(false)
                    _loginSuccess.value = true
                } else {
                    _showMessage.value = "Child login failed"
                    Log.d("test", "Child username not found")
                }
            }
            .addOnFailureListener {
                _showMessage.value = "Child login failed"
                Log.d("test", "Child login failed")
            }
    }

    fun signInWithParent(
        email: String,
        password: String,
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _showMessage.value = "Login failed!"
                } else {
                    preferencesManager.saveUser(firebaseAuth.currentUser?.uid)
                    preferencesManager.setIsParent(true)
                    _loginSuccess.value = true
                }
            }
            .addOnFailureListener {
                _showMessage.value = "Login failed!"
            }
    }
}

