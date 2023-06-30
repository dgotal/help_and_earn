package com.d1v0r.help_and_earn.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d1v0r.help_and_earn.model.Task

@Composable
fun ChildScreen() {
    val viewModel: ChildViewModel = viewModel()
    val items: List<Task> by viewModel.items.collectAsState(emptyList())

    Column {
        ChildScreenHeader()
        ChildTasksRecyclerView(items)
        ChildScreenFooter()
    }
}

@Composable
fun ChildScreenHeader() {
    Text(text = "Child Screen")
}

@Composable
fun ChildTasksRecyclerView(items: List<Task>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(items) { task ->
            TaskItem(task = task)
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Deadline: ${task.deadline}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Reward: ${task.coinReward}")
            // Add more task details as needed
        }
    }
}

@Composable
fun ChildScreenFooter() {
}
