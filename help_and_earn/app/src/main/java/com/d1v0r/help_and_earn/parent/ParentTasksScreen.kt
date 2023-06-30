package com.d1v0r.help_and_earn.parent

import ParentViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d1v0r.help_and_earn.model.Task

@Composable
fun ParentTasksScreen() {
    val viewModel: ParentViewModel = viewModel()
    SetTasks(
        viewModel = viewModel,
        onDeleteTask = { viewModel.deleteTask(it) },
        onAddTask = { viewModel.addTask(it) }
    )
}

@Composable
fun TaskItem(task: Task, onDeleteTask: (Task) -> Unit) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Column(Modifier.weight(1f)) {
            Text("Title: ${task.title}")
            Text("Deadline: ${task.deadline}")
            Text("Coin Reward: ${task.coinReward}")
        }
        Button(
            onClick = { onDeleteTask(task) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Delete")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTasks(
    viewModel: ParentViewModel,
    onDeleteTask: (Task) -> Unit,
    onAddTask: (Task) -> Unit
) {
    var isAddingTask by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDeadline by remember { mutableStateOf("") }
    var newTaskCoinReward by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val tasks by viewModel.items.collectAsState()
        LazyColumn(Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onDeleteTask = { onDeleteTask(it) }
                )
            }
        }
        if (isAddingTask) {
            Column(Modifier.padding(16.dp)) {
                TextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Title") }
                )

                Spacer(Modifier.padding(8.dp))

                TextField(
                    value = newTaskDeadline,
                    onValueChange = { newTaskDeadline = it },
                    label = { Text("Deadline") }
                )

                Spacer(Modifier.padding(8.dp))

                TextField(
                    value = newTaskCoinReward.toString(),
                    onValueChange = { newTaskCoinReward = it.toIntOrNull() ?: 0 },
                    label = { Text("Coin Reward") }
                )

                Spacer(Modifier.padding(8.dp))

                Row(Modifier.padding(top = 8.dp)) {
                    Button(
                        onClick = {
                            onAddTask(
                                Task(
                                    title = newTaskTitle,
                                    deadline = newTaskDeadline,
                                    coinReward = newTaskCoinReward
                                )
                            )
                            isAddingTask = false
                            newTaskTitle = ""
                            newTaskDeadline = ""
                            newTaskCoinReward = 0
                        }
                    ) {
                        Text("Add Task")
                    }

                    Spacer(Modifier.padding(start = 8.dp))

                    Button(
                        onClick = { isAddingTask = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
        Button(
            onClick = { isAddingTask = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Task")
        }
    }
}