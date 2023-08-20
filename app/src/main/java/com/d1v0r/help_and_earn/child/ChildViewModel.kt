package com.d1v0r.help_and_earn.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.PreferencesManager
import com.d1v0r.help_and_earn.firebase.FirebaseDateFormatter
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Task
import com.d1v0r.help_and_earn.model.Wishlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChildViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val collectionRef = firestore.collection("Tasks")
    private val wishlistCollectionRef = firestore.collection("wishlist")

    private val _items: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Task>> = _items

    private val _wishlistItems: MutableStateFlow<List<Wishlist>> = MutableStateFlow(emptyList())
    val wishlistItems: StateFlow<List<Wishlist>> = _wishlistItems

    private val _child: MutableStateFlow<Child> = MutableStateFlow(Child())
    val child: StateFlow<Child> = _child

    init {
        fetchTasks()
        fetchWishlistItems()
        fetchChildData()
    }

    fun getChildImagePath(): String {
        return _child.value.imagePath
    }

    fun declineTask(task: Task) {
        val taskDocumentRef = collectionRef.document(task.id)
        val updatedTask = task.copy(declined = true)
        taskDocumentRef.set(updatedTask)
            .addOnSuccessListener {
                println("Task declined: $task")
                fetchTasks()
            }
            .addOnFailureListener { exception ->
                println("Error declining task: $exception")
            }
    }

    fun sendToReview(task: Task) {
        val taskDocumentRef = collectionRef.document(task.id)
        val updatedTask = task.copy(childApproved = true)
        taskDocumentRef.set(updatedTask)
            .addOnSuccessListener {
                println("Task sent to review: $task")
                fetchTasks()
            }
            .addOnFailureListener { exception ->
                println("Error sending task to review: $exception")
            }
    }

    private fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskResult = collectionRef
                    .whereEqualTo("childId", preferencesManager.getUser())
                    .get().await()

                _items.value = taskResult.mapNotNull {
                    it.toObject(Task::class.java)
                }.sortedBy { FirebaseDateFormatter.stringToDate(it.deadline) }
            } catch (e: Exception) {
                println("Error while fetching tasks: ${e.message}")
            }
        }
    }

    private fun fetchWishlistItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskResult = wishlistCollectionRef
                    .whereEqualTo("childId", preferencesManager.getUser())
                    .get().await()

                _wishlistItems.value = taskResult.mapNotNull {
                    it.toObject(Wishlist::class.java)
                }.sortedBy { it.itemName }
            } catch (e: Exception) {
                println("Error while fetching wishlist: ${e.message}")
            }
        }
    }

    fun clearUserData() {
        preferencesManager.clearUserChildData()
        firebaseAuth.signOut()
    }

    private fun fetchChildData() {
        val currentUserId = preferencesManager.getUser()
        val childrenCollectionRef = firestore.collection("children")

        childrenCollectionRef.whereEqualTo("id", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val childData = querySnapshot.documents.first().toObject(Child::class.java)
                    if (childData != null) {
                        _child.value = childData
                    }
                    println("Fetched Child Data: $childData")
                } else {
                    println("Child data document doesn't exist")
                }
            }
            .addOnFailureListener { exception ->
                println("Failed to fetch child data: $exception")
            }
    }
    fun buyWishlistItem(wishlistItem: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentRef = wishlistCollectionRef.document(wishlistItem.id)
                documentRef.update("bought", true).await()
                updateChildBalance(wishlistItem)
                fetchWishlistItems()
            } catch (e: Exception) {
                println("Error marking wishlist item as bought: ${e.message}")
            }
        }
    }
    private fun updateChildBalance(wishlistItem: Wishlist) {
        val childData = child.value
        if (childData.balance >= wishlistItem.price) {
            val updatedBalance = childData.balance - wishlistItem.price
            val childDocumentRef = firestore.collection("children")
            childDocumentRef
                .whereEqualTo("id", childData.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        documentSnapshot.reference.update("balance", updatedBalance)
                            .addOnSuccessListener {
                                println("Child balance updated successfully")
                            }
                            .addOnFailureListener { exception ->
                                println("Error updating child balance: ${exception.message}")
                            }
                    } else {
                        println("Child data document doesn't exist")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Failed to fetch child data: ${exception.message}")
                }
        } else {
            println("Cannot update child balance. Either childData is null or balance is insufficient.")
        }
    }



    fun saveAvatar(selectedAvatarIndex: String) {
        val currentUserId = preferencesManager.getUser()
        val childrenCollectionRef = firestore.collection("children")

        childrenCollectionRef.whereEqualTo("id", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    documentSnapshot.reference.update("imagePath", selectedAvatarIndex)
                        .addOnSuccessListener {
                            println("Image path updated.")
                        }
                        .addOnFailureListener { exception ->
                            println("Error updating image path: ${exception.message}")
                        }
                } else {
                    println("Child data document doesn't exist")
                }
            }
    }
}