package com.d1v0r.help_and_earn.parent

import ParentViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d1v0r.help_and_earn.model.Task

@Composable
fun ParentApproveTasksScreen() {
    val parentViewModel: ParentViewModel = viewModel()

    val tasks = parentViewModel.items.collectAsState().value
        .filter { it.childApproved }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(task = task)
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Text("Title: ${task.title}")
    Text("Deadline: ${task.deadline}")
    Text("Coin Reward: ${task.coinReward}")
}
