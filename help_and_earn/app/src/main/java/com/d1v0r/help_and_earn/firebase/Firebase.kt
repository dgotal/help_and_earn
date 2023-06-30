package com.d1v0r.help_and_earn.firebase

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseViewModel : ViewModel() {
    //LOGIKA ZA RAD S FIREBASEOM - spremanja podataka / preuzimanje / provjera podataka LOGIN REGISTRACIJU
    //OVU KLASU POZIVAM KADA NEŠTO ZELIM RADITI S FIREBASEOM IZ VIEWMODELA
    //A U VIEWMODELU IMAM LOGIKU I POZIVAM FIREBASE, A U SCREENU DEFINIRAM UI

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null

    fun getCurrentUser(): FirebaseUser? {
        return currentUser
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            currentUser = result.user
            true
        } catch (e: Exception) {
            // Greška pri prijavljivanju
            false
        }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
    }
}