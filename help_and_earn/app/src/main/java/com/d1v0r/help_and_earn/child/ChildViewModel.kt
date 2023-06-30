package com.d1v0r.help_and_earn.child

import androidx.lifecycle.ViewModel
import com.d1v0r.help_and_earn.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChildViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()
    private val collectionRef = database.collection("Tasks")

    private val _items: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Task>> = _items

    init {
        collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val taskList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Task::class.java)
                }
                _items.value = taskList
            }
        }
    }
}



