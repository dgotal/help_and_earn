package com.d1v0r.help_and_earn.parent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.PreferencesManager
import com.d1v0r.help_and_earn.model.Child
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
class WishlistViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) :
    ViewModel() {
    private val wishlistCollectionRef = firestore.collection("wishlist")

    private val _wishlistItems: MutableStateFlow<List<Wishlist>> = MutableStateFlow(emptyList())
    val wishlistItems: StateFlow<List<Wishlist>> = _wishlistItems

    private val _child: MutableStateFlow<Child?> = MutableStateFlow(null)
    val child: StateFlow<Child?> = _child

    init {
        fetchWishlistItems()
        fetchChildData()
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

    fun loadWishlist(childId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            wishlistCollectionRef
                .whereEqualTo("childId", childId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val wishlistItems = snapshot.documents.mapNotNull { document ->
                            document.toObject(Wishlist::class.java)
                        }.sortedBy { it.itemName }
                        _wishlistItems.value = wishlistItems
                    }
                }
        }
    }

    fun updateWishlistItem(updatedItem: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentRef = wishlistCollectionRef.document(updatedItem.id)
                documentRef.update("itemName", updatedItem.itemName, "price", updatedItem.price)
                    .await()
            } catch (e: Exception) {
                println("Error updating wishlist item: ${e.message}")
            }
        }
    }

    fun deleteWishlistItem(itemToDelete: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentRef = wishlistCollectionRef.document(itemToDelete.id)
                documentRef.delete().await()
            } catch (e: Exception) {
                println("Error deleting wishlist item: ${e.message}")
            }
        }
    }

    fun addWish(wish: Wishlist): Wishlist {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentRef = wishlistCollectionRef.document()
                wish.id = documentRef.id
                wish.parentId = firebaseAuth.currentUser?.uid ?: ""
                documentRef.set(wish).await()
                println("Wishlist item created successfully with ID: ${wish.id}")
            } catch (e: Exception) {
                println("Error creating wishlist item: ${e.message}")
            }
        }
        return wish
    }

}
