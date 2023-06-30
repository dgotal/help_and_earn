import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParentViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()
    private val collectionRef = database.collection("Tasks")
    private val childCollectionRef = database.collection("children")

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

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                val documentRef = collectionRef.document()
                task.id = documentRef.id
                documentRef.set(task).await()
                println("Task created successfully with ID: ${task.id}")
            } catch (e: Exception) {
                println("Error creating task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            collectionRef.document(task.id).delete()
        }
    }

    fun getChildren(parentId: String): StateFlow<List<Child>> {
        val children = MutableStateFlow(emptyList<Child>())
        viewModelScope.launch {
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
                        children.value = childList
                    }
                }
        }
        return children
    }

    fun getChildTasks(parentId: String, childId: String): StateFlow<List<Task>> {
        val childTasks = MutableStateFlow(emptyList<Task>())
        viewModelScope.launch {
            collectionRef
                .whereEqualTo("parentId", parentId)
                .whereEqualTo("childId", childId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val taskList = snapshot.documents.mapNotNull { document ->
                            document.toObject(Task::class.java)
                        }
                        childTasks.value = taskList
                    }
                }
        }
        return childTasks
    }

    fun approveTask(task: Task) {
        viewModelScope.launch {
            try {
                updateChildBalance(task)
                updateTaskApproval(task)
                removeTaskFromList(task)
            } catch (e: Exception) {
                println("Error approving task: ${e.message}")
            }
        }
    }

    private suspend fun updateChildBalance(task: Task) {
        val childDocumentRef = childCollectionRef.document(task.childId)
        val childDocumentSnapshot = childDocumentRef.get().await()
        val child = childDocumentSnapshot.toObject(Child::class.java)

        if (child != null) {
            val updatedBalance = child.balance + task.coinReward
            childDocumentRef.update("balance", updatedBalance).await()
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
    }
}
