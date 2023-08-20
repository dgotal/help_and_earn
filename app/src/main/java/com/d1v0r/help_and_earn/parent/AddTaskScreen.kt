package com.d1v0r.help_and_earn.parent

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.firebase.FirebaseDateFormatter
import com.d1v0r.help_and_earn.model.Task
import com.d1v0r.help_and_earn.parent.viewmodel.TaskViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun AddTask(
    navController: NavHostController,
    childId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Task", color = Color.White, fontSize = 22.sp) },
                backgroundColor = Color(0xFF995825)
            )
        },
        backgroundColor = Color(0x19995825),
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFB3E5FC)
            ) {
                ParentChildClickedBottomBar(navController, childId)
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x8BFFFFFF)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x8BFFFFFF),
                ),
                shape = RoundedCornerShape(16.dp),
                content = {
                    Text(
                        text = "Create a new task for your child",
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xBF000000),
                        modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AddTaskForm(childId, navController)
                }
            )
            val taskImage: Painter = painterResource(id = R.drawable.task)
            Image(painter = taskImage, contentDescription = "Task")
        }
    }
}

@Composable
fun AddTaskForm(childId: String, navController: NavHostController) {
    val context = LocalContext.current

    val taskViewModel: TaskViewModel = hiltViewModel()
    var title by remember { mutableStateOf(TextFieldValue()) }
    var deadline by remember { mutableStateOf("") }
    var coinReward by remember { mutableStateOf(TextFieldValue()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(
                    0xFF995825
                ), focusedLabelColor = Color(0xFF995825)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { /* Handle Next */ }),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedTextField(
                value = deadline,
                onValueChange = { },
                label = { Text("Deadline") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(
                        0xFF995825
                    ), focusedLabelColor = Color(0xFF995825)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    deadline = selectedDate
                },
                onDismiss = {
                    isDatePickerVisible = false
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = coinReward,
            onValueChange = { coinReward = it },
            label = { Text("Coin Reward") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(
                    0xFF995825
                ), focusedLabelColor = Color(0xFF995825)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddTaskButton {
                if (taskViewModel.hasError(title.text, coinReward.text, deadline)) {
                    Toast.makeText(context, "Please check all fields!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    taskViewModel.addTask(
                        Task(
                            title = title.text,
                            deadline = deadline,
                            coinReward = coinReward.text.toInt(),
                            childId = childId
                        )
                    )
                    navController.navigate("parentChildClickedScreen/$childId")
                }
            }
        }

    }
}

@Composable
fun AddTaskButton(onAddTaskClick: () -> Unit) {
    Button(
        onClick = onAddTaskClick,
        modifier = Modifier
            .wrapContentSize()
            .padding(24.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF995825),
            contentColor = Color.White
        )
    ) {
        Text(text = "Add Task", textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerAlertDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        FirebaseDateFormatter.dateToString(Date(it))
    }
    val currentDateMillis = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF995825),
                    contentColor = Color.White
                ), onClick = {
                    selectedDate?.let { onDateSelected(it) }
                    onDismiss()
                }

            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF995825),
                    contentColor = Color.White
                ), onClick = {
                    onDismiss()
                }) {
                Text(text = "Cancel")
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            dateValidator = { timestamp ->
                timestamp >= currentDateMillis
            }
        )
    }
}
