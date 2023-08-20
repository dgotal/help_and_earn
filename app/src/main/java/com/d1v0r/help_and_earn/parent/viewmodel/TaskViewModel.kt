package com.d1v0r.help_and_earn.parent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.firebase.FirebaseDateFormatter
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : ViewModel() {
    private val collectionRef = firestore.collection("Tasks")
    private val childCollectionRef = firestore.collection("children")

    private val _items: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Task>> = _items

    private val tasks: MutableList<Task> = mutableListOf()

    private var loadTasksJob: Job? = null

    init {
        checkAndDeclineTasks()
    }

    fun loadTasks(childId: String) {
        loadTasksJob?.cancel()
        loadTasksJob = viewModelScope.launch(Dispatchers.IO) {
            collectionRef
                .whereEqualTo("childId", childId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val taskList = snapshot?.documents?.mapNotNull { document ->
                        document.toObject(Task::class.java)
                    }?.sortedBy { FirebaseDateFormatter.stringToDate(it.deadline) } ?: emptyList()

                    _items.value = taskList

                    tasks.clear()
                    tasks.addAll(taskList)
                }
        }
    }

    private fun checkAndDeclineTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = Calendar.getInstance()
            currentDate.add(Calendar.DATE, -1)
            val tasksToDecline = _items.value.filter { task ->
                val deadlineDate = FirebaseDateFormatter.stringToDate(task.deadline)
                !task.declined && !task.childApproved && deadlineDate != null && deadlineDate.before(
                    currentDate.time
                )
            }
            tasksToDecline.forEach { task ->
                performDeclineTask(task)
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentRef = collectionRef.document()
                task.id = documentRef.id
                task.parentId = firebaseAuth.currentUser?.uid ?: ""
                documentRef.set(task).await()
                println("Task created successfully with ID: ${task.id}")
            } catch (e: Exception) {
                println("Error creating task: ${e.message}")
            }
        }
    }

    fun hasError(title: String, reward: String, date: String): Boolean {
        return title.isBlank() || reward.isBlank() || date.isBlank()
    }

    fun editTask(updatedTask: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskDocumentRef = collectionRef.document(updatedTask.id)
                taskDocumentRef.update(
                    "title", updatedTask.title,
                    "coinReward", updatedTask.coinReward,
                    "deadline", updatedTask.deadline
                ).await()

                val clonedList = ArrayList(_items.value)
                val index = _items.value.indexOfFirst { it.childId == updatedTask.childId }

                clonedList[index] = clonedList[index].copy(
                    title = updatedTask.title,
                    coinReward = updatedTask.coinReward,
                    deadline = updatedTask.deadline
                )

                _items.emit(clonedList)

                println("Task edited successfully: ${updatedTask.id}")
            } catch (e: Exception) {
                println("Error editing task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRef.document(task.id).delete()
        }
    }

    fun approveTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateChildBalance(task)
                updateTaskApproval(task)
                removeTaskFromList(task)
            } catch (e: Exception) {
                println("Error approving task: ${e.message}")
            }
        }
    }

    fun filterBySearch(value: String) {
        _items.value = tasks.filter { it.title.contains(value, true) }
    }

    private suspend fun getChildDocumentIdByChildId(childId: String): String? {
        val query = childCollectionRef.whereEqualTo("id", childId)

        try {
            val querySnapshot = query.get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                return documentSnapshot?.id
            }
        } catch (e: Exception) {
            println("Error getting child document ID: ${e.message}")
        }

        return null
    }

    private suspend fun updateChildBalance(task: Task) {
        val childId = task.childId
        val childDocumentId = getChildDocumentIdByChildId(childId)

        if (childDocumentId != null) {
            val childDocumentRef = childCollectionRef.document(childDocumentId)

            try {
                val childDocumentSnapshot = childDocumentRef.get().await()

                if (childDocumentSnapshot.exists()) {
                    val child = childDocumentSnapshot.toObject(Child::class.java)

                    if (child != null) {
                        val updatedBalance = child.balance + task.coinReward
                        childDocumentRef.update("balance", updatedBalance).await()
                        println("Child balance updated successfully: $updatedBalance")
                    }
                }
            } catch (e: Exception) {
                println("Error updating child balance: ${e.message}")
            }
        } else {
            println("Child not found with childId: $childId")
        }
    }

    private suspend fun updateTaskApproval(task: Task) {
        collectionRef.document(task.id)
            .update("parentApproved", true)
            .await()
    }

    private fun removeTaskFromList(task: Task) {
        val updatedTaskList = _items.value.toMutableList()
        updatedTaskList.remove(task)
        _items.value = updatedTaskList

        tasks.clear()
        tasks.addAll(updatedTaskList)
    }

    fun declineTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateTaskMessageAndApproval(task, false)
                removeTaskFromList(task)
            } catch (e: Exception) {
                println("Error declining task: ${e.message}")
            }
        }
    }

    private suspend fun updateTaskMessageAndApproval(task: Task, childApproved: Boolean) {
        val updates = mapOf(
            "message" to task.message,
            "childApproved" to childApproved,
            "parentApproved" to false
        )

        collectionRef.document(task.id)
            .update(updates)
            .await()
    }

    private suspend fun performDeclineTask(task: Task) {
        try {
            collectionRef.document(task.id)
                .update("declined", true)
                .await()
        } catch (e: Exception) {
            println("Error declining task: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadTasksJob?.cancel()
    }
}