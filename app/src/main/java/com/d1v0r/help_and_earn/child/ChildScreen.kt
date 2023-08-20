package com.d1v0r.help_and_earn.child

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.model.Task

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChildScreen(
    navController: NavHostController,
) {
    val viewModel: ChildViewModel = hiltViewModel()
    val items: List<Task> by viewModel.items.collectAsState()

    val tabTitles = listOf("Active", "On Review", "Done")
    val selectedTabIndex = rememberTabState()

    Scaffold(
        topBar = {
            ChildScreenHeader()
        },
        bottomBar = {
            ChildBottomBar(navController)
        })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x11864733),
                contentColor = Color(0xFF804313)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = { selectedTabIndex.value = index },
                        text = { androidx.compose.material.Text(title) }
                    )
                }
            }
            AnimatedContent(
                targetState = selectedTabIndex.value,
                label = ""
            ) { targetState ->
                val filteredItems = when (targetState) {
                    TaskTab.Active.ordinal -> items.filter { !it.childApproved && !it.parentApproved && !it.declined }
                    TaskTab.Done.ordinal -> items.filter { it.childApproved && it.parentApproved }
                    else -> items.filter { it.childApproved && !it.parentApproved }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(filteredItems) { task ->
                        TaskItem(task = task)
                    }
                }

            }
        }
    }
}


@Composable
fun ChildScreenHeader() {
    TopAppBar(
        title = {
            Text(
                text = "Home",
                color = Color.White,
                fontSize = 22.sp
            )
        },
        backgroundColor = Color(0xFF995825)
    )
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                    Row {
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
                if (!task.childApproved && !task.parentApproved) {
                    if (task.message.isNotBlank()) {
                        Row {
                            Text(
                                "Note: ${task.message}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp, top = 16.dp),
                                color = MaterialTheme.colors.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        ActiveTaskButtons(task)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTaskButtons(task: Task) {
    val viewModel: ChildViewModel = hiltViewModel()
    val showDoneDialog = remember { mutableStateOf(false) }
    val showDeclineDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showDeclineDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Decline", color = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { showDoneDialog.value = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Done", color = Color.White)
        }

        if (showDoneDialog.value) {
            ShowDoneAlertDialog(
                onConfirm = {
                    viewModel.sendToReview(task)
                },
                onDismiss = {
                    showDoneDialog.value = false
                }
            )
        }

        if (showDeclineDialog.value) {
            ShowDeclineAlertDialog(
                onConfirm = {
                    viewModel.declineTask(task)
                },
                onDismiss = {
                    showDeclineDialog.value = false
                }
            )
        }
    }
}

@Composable
fun ChildBottomBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        BottomNavigationItem(
            selected = currentDestination?.hierarchy?.any { it.route == "childCartScreen" } == true,
            onClick = {
                navController.navigate("childCartScreen") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Shop, contentDescription = "Cart") },
            label = { androidx.compose.material.Text("Cart") },
            selectedContentColor = Color(0xFF814A1E)
        )
        BottomNavigationItem(
            selected = currentDestination?.hierarchy?.any { it.route == "childScreen" } == true,
            onClick = {
                navController.navigate("childScreen") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { androidx.compose.material.Text("Home") },
            selectedContentColor = Color(0xFF814A1E)
        )
        BottomNavigationItem(
            selected = currentDestination?.hierarchy?.any { it.route == "childProfileScreen" } == true,
            onClick = {
                navController.navigate("childProfileScreen") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { androidx.compose.material.Text("Profile") },
            selectedContentColor = Color(0xFF814A1E)
        )
    }
}

@Composable
fun TaskTabs(selectedTab: TaskTab, onTabSelected: (TaskTab) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x11864733),
        contentColor = Color(0xFF804313),
    ) {
        TaskTab.values().forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.title, color = Color(0xFF804313)) }
            )
        }
    }
}

@Composable
fun ShowDoneAlertDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change status") },
        text = { Text("Are you sure you want to mark this task as done?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun ShowDeclineAlertDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change status") },
        text = { Text("Are you sure you want to decline this task?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

enum class TaskTab(val title: String) {
    Active("Active"),
    OnReview("On Review"),
    Done("Done")
}
