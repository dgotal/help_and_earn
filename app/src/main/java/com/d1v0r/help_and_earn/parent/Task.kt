package com.d1v0r.help_and_earn.parent

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.model.Task
import com.d1v0r.help_and_earn.parent.viewmodel.TaskViewModel

@Composable
fun TaskItem(
    task: Task,
    onDeleteTask: (Task) -> Unit,
    onApproveTask: (Task) -> Unit,
    onDeclineTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color.White,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    task.title,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text(text = "Deadline: ", fontSize = 14.sp)
                        Text(task.deadline, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    //GoldenCircle(coinReward = task.coinReward)
                    Row()
                    {
                        Text(
                            task.coinReward.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.coin_icon),
                            contentDescription = "Coin",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                if (task.childApproved && !task.parentApproved && !task.declined) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        TaskDeclineButton(task)
                        Spacer(modifier = Modifier.width(8.dp))
                        TaskApproveButton(task = task, onApproveTask = onApproveTask)
                    }
                } else if (!task.childApproved && !task.parentApproved && !task.declined) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        TaskDeleteButton(task, onDeleteTask)
                        Spacer(modifier = Modifier.width(8.dp))
                        TaskEditButton(task, onEditTask)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDeleteButton(task: Task, onDeleteTask: (Task) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Delete", color = Color.White)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTask(task)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun TaskEditButton(task: Task, onEditTask: (Task) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Edit", color = Color.White)
    }

    if (showDialog) {
        UpdateTaskItemDialog(
            initialTaskTitle = task.title,
            initialTaskCoinReward = task.coinReward,
            initialTaskDeadline = task.deadline,
            onConfirm = { newItemName, newDeadline, newPrice ->
                if (newItemName.isBlank() || newDeadline.isBlank() || newPrice.isBlank()) {
                    Toast.makeText(context, "Please check all fields!", Toast.LENGTH_SHORT).show()
                } else {
                    task.title = newItemName
                    task.deadline = newDeadline
                    task.coinReward = newPrice.toInt()

                    onEditTask(task)
                }
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
fun TaskApproveButton(task: Task, onApproveTask: (Task) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Approve", color = Color.White)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Approve Task") },
            text = { Text("Are you sure you want to approve this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        onApproveTask(task)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
                ) {
                    Text("Approve", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}


@Composable
fun TaskDeclineButton(task: Task) {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: TaskViewModel = hiltViewModel()

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Decline", color = Color.White)
    }

    if (showDialog) {
        DeclineAlertDialog(onCancelButtonClick = { showDialog = false },
            onSendButtonClick = { declinedMessage ->
                task.message = declinedMessage
                viewModel.declineTask(task)
                showDialog = false
            })
    }
}

@Composable
fun SearchBar(onTextChange: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChange(it)
        },
        label = { Text("Search") },
        placeholder = { Text("Search tasks") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF995825),
            focusedLabelColor = Color(0xFF995825),
            cursorColor = Color(0xFF995825)
        )
    )
}

@Composable
fun UpdateTaskItemDialog(
    initialTaskTitle: String,
    initialTaskDeadline: String,
    initialTaskCoinReward: Int,
    onConfirm: (newTitle: String, newDeadline: String, newPrice: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newItemName by remember { mutableStateOf(initialTaskTitle) }
    var newTaskDeadline by remember { mutableStateOf(initialTaskDeadline) }
    var newPrice by remember { mutableStateOf(initialTaskCoinReward.toString()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit task",
                color = Color(0xFF995825),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Task title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF995825),
                        focusedLabelColor = Color(0xFF995825),
                        cursorColor = Color(0xFF995825)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = newTaskDeadline,
                        onValueChange = { },
                        label = { Text("Deadline") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(
                                0xFF995825
                            ), focusedLabelColor = Color(0xFF995825)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0f)
                            .clickable(onClick = {
                                isDatePickerVisible = true
                            }),
                    )
                }
                if (isDatePickerVisible) {
                    DatePickerAlertDialog(
                        onDateSelected = { selectedDate ->
                            newTaskDeadline = selectedDate
                        },
                        onDismiss = {
                            isDatePickerVisible = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("Task CoinReward") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF995825),
                        focusedLabelColor = Color(0xFF995825),
                        cursorColor = Color(0xFF995825)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(newItemName, newTaskDeadline, newPrice)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825))
            ) {
                Text("Edit", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

enum class Tab(val title: String) {
    Done("Done"),
    Pending("Pending"),
    Declined("Declined"),
    Active("Active")
}
