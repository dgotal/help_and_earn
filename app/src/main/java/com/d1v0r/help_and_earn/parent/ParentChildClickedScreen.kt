package com.d1v0r.help_and_earn.parent

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d1v0r.help_and_earn.parent.viewmodel.ParentViewModel
import com.d1v0r.help_and_earn.parent.viewmodel.TaskViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ParentChildClickedScreen(
    navController: NavHostController,
    childId: String,
) {
    val parentViewModel: ParentViewModel = hiltViewModel()
    val tasksViewModel: TaskViewModel = hiltViewModel()
    parentViewModel.getChild(childId)
    tasksViewModel.loadTasks(childId)

    var isDeclineDialogVisible by remember { mutableStateOf(false) }
    val child by parentViewModel.child.collectAsState()

    val childTasks by tasksViewModel.items.collectAsState()
    var selectedTab by remember { mutableStateOf(Tab.Pending) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks - ${child?.fullName}") },
                actions = {
                    IconButton(onClick = { navController.navigate("addTask/$childId") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }, backgroundColor = Color(0xFF995825), contentColor = Color.White
            )
        },
        backgroundColor = Color.White,
        bottomBar = {
            ParentChildClickedBottomBar(navController, childId)
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x11864733),
                contentColor = Color(0xFF804313)
            ) {
                Tab(selected = selectedTab == Tab.Done,
                    onClick = { selectedTab = Tab.Done },
                    text = { Text("Done") })
                Tab(
                    selected = selectedTab == Tab.Pending,
                    onClick = { selectedTab = Tab.Pending },
                    text = { Text("Pending") },
                )
                Tab(selected = selectedTab == Tab.Declined,
                    onClick = { selectedTab = Tab.Declined },
                    text = { Text("Declined") })
                Tab(selected = selectedTab == Tab.Active,
                    onClick = { selectedTab = Tab.Active },
                    text = { Text("Active") })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SearchBar {
                    tasksViewModel.filterBySearch(it)
                }
            }
            AnimatedContent(
                targetState = selectedTab,
                label = ""
            ) { targetState ->
                val filteredItems = when (targetState) {
                    Tab.Done -> childTasks.filter { it.parentApproved && it.childApproved && !it.declined }
                    Tab.Pending -> childTasks.filter { it.childApproved && !it.parentApproved && !it.declined }
                    Tab.Declined -> childTasks.filter { it.declined }
                    Tab.Active -> childTasks.filter { !it.childApproved && !it.parentApproved && !it.declined }
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = filteredItems, itemContent = { task ->
                        TaskItem(
                            task = task,
                            onDeleteTask = { tasksViewModel.deleteTask(task) },
                            onApproveTask = { tasksViewModel.approveTask(task) },
                            onDeclineTask = {
                                tasksViewModel.declineTask(task)
                                isDeclineDialogVisible = true
                            },
                            onEditTask = { tasksViewModel.editTask(task) }
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun ParentChildClickedBottomBar(navController: NavHostController, childId: String) {
    BottomNavigation(
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        BottomNavigationItem(selected = currentDestination?.hierarchy?.any { it.route == "parentChildClickedScreen/{childId}" } == true,
            onClick = {
                navController.navigate("parentScreen") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selectedContentColor = Color(0xFF814A1E)
        )
        BottomNavigationItem(selected = currentDestination?.hierarchy?.any { it.route == "wishlist/{childId}" } == true,
            onClick = {
                navController.navigate("wishlist/$childId") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.List, contentDescription = "Wishlist") },
            label = { Text("Wishlist") },
            selectedContentColor = Color(0xFF814A1E)
        )
    }
}

@Composable
fun DeclineAlertDialog(
    onCancelButtonClick: () -> Unit, onSendButtonClick: (String) -> Unit
) {
    var declineMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCancelButtonClick() },
        title = { Text("Enter decline message") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    value = declineMessage,
                    onValueChange = { declineMessage = it },
                    textStyle = TextStyle(color = Color(0xFF814A1E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.1f)
                        .background(Color(0xD995825))
                        .padding(8.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSendButtonClick(declineMessage)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825))
            ) {
                Text("Send", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onCancelButtonClick() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
