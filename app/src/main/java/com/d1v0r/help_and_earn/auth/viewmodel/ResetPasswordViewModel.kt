package com.d1v0r.help_and_earn.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _resetPasswordSuccess: MutableStateFlow<String?> = MutableStateFlow(null)
    val resetPasswordSuccess: StateFlow<String?> = _resetPasswordSuccess.asStateFlow()
    fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _resetPasswordSuccess.value = "Email successfully sent!"
                } else {
                    _resetPasswordSuccess.value = "Email doesn't exist!"
                }
            }
    }
}