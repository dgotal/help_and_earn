package com.d1v0r.help_and_earn.parent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.PreferencesManager
import com.d1v0r.help_and_earn.firebase.FirebaseDateFormatter
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Parent
import com.d1v0r.help_and_earn.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ParentViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : ViewModel() {
    private val childCollectionRef = firestore.collection("children")
    private val parentCollectionRef = firestore.collection("users")

    private val _child: MutableStateFlow<Child?> = MutableStateFlow(null)
    val child: StateFlow<Child?> = _child

    private val _parent: MutableStateFlow<Parent> = MutableStateFlow(Parent())
    val parent: StateFlow<Parent> = _parent

    private val _showMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val showMessage: StateFlow<String?> = _showMessage

    private val _successCreateChild: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val successCreateChild: StateFlow<Boolean?> = _successCreateChild

    init {
        fetchParent()
    }

    fun addNewChild(child: Child) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usernameExist = checkUsernameExist(child.username)
                if (usernameExist){
                    _showMessage.value = "Child with same username already exists!"
                }
                else {
                    val documentRef = childCollectionRef.document()
                    child.id = documentRef.id
                    child.parentId = firebaseAuth.currentUser?.uid ?: ""

                    if (child.imagePath.isBlank()) {
                        child.imagePath = "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar1.png?alt=media&token=526a63cc-0187-4b23-802e-30e37e0d9b32"
                    }

                    documentRef.set(child).await()
                    _successCreateChild.value = true
                    println("Child added successfully with ID: ${child.id}")
                }
            } catch (e: Exception) {
                println("Error adding child: ${e.message}")
            }
        }
    }

    private suspend fun checkUsernameExist(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result =
                childCollectionRef
                    .whereEqualTo("username", username).get().await()

            return@withContext !result.isEmpty
        }

    }

    fun getChildren(): StateFlow<List<Child>> {
        val parentId = firebaseAuth.currentUser?.uid
        val children = MutableStateFlow(emptyList<Child>())
        viewModelScope.launch(Dispatchers.IO) {
            childCollectionRef
                .whereEqualTo("parentId", parentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val childList = snapshot.documents.mapNotNull { document ->
                            document.toObject(Child::class.java)
                        }
                        children.value = childList.sortedBy { it.fullName }
                    }
                }
        }
        return children
    }

    fun getChild(childId: String? = null) {
        if (childId == null) {
            _child.value = null
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            childCollectionRef
                .whereEqualTo("id", childId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val childList = snapshot.documents.mapNotNull { document ->
                            document.toObject(Child::class.java)
                        }
                        _child.value = childList.first()
                    }
                }
        }
    }

    fun clearUserData() {
        preferencesManager.clearUserParentData()
        firebaseAuth.signOut()
    }

    private fun fetchParent() {
        val currentUserId = preferencesManager.getUser()

        viewModelScope.launch(Dispatchers.IO) {
            if (currentUserId != null) {
                parentCollectionRef.document(currentUserId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val parentData = documentSnapshot.toObject(Parent::class.java)
                            val imagePath = parentData?.imagePath
                            //parent.value.imagePath = imagePath.toString()
                            if (parentData != null) {
                                _parent.value = parentData
                            }
                        } else {
                            println("Parent data document doesn't exist")
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Failed to fetch parent data: $exception")
                    }
            }
        }
    }

    fun saveAvatarParent(imageUrl: String) {
        val currentUserId = preferencesManager.getUser()
        if (currentUserId != null) {
            parentCollectionRef.document(currentUserId)
                .update("imagePath", imageUrl)
                .addOnSuccessListener {
                    println("Parent's image path updated.")
                }
                .addOnFailureListener { exception ->
                    println("Error updating parent's image path: ${exception.message}")
                }
        } else {
            println("ERROR")
        }
    }
}
