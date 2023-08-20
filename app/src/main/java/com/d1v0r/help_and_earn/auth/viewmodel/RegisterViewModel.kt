package com.d1v0r.help_and_earn.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Parent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val usersCollectionRef = firestore.collection("users")
    private val childrenCollectionRef = firestore.collection("children")

    private val _registrationSuccess: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val registrationSuccess: StateFlow<Boolean?> = _registrationSuccess.asStateFlow()

    fun registerUser(parent: Parent) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId =
                    firebaseAuth.createUserWithEmailAndPassword(parent.email, parent.password)
                        .await()
                        .user
                        ?.uid
                if (userId != null) {
                    parent.id = userId
                    usersCollectionRef.document(userId).set(parent).await()
                }
                _registrationSuccess.value = true
                println("User registered successfully with ID: ${parent.id}")
            } catch (e: Exception) {
                _registrationSuccess.value = false
                println("Error registering user: ${e.message}")
            }
        }
    }

    fun registerChildUser(child: Child) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId =
                    firebaseAuth.createUserWithEmailAndPassword(child.username, child.passcode)
                        .await()
                        .user
                        ?.uid
                if (userId != null) {
                    child.id = userId
                    childrenCollectionRef.document(userId).set(child).await()
                }
                _registrationSuccess.value = true
                println("User registered successfully with ID: ${child.id}")
            } catch (e: Exception) {
                _registrationSuccess.value = false
                println("Error registering user: ${e.message}")
            }
        }
    }
}
