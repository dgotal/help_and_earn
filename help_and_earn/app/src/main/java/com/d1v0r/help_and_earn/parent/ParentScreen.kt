package com.d1v0r.help_and_earn.parent

import ParentViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.model.Child

@Composable
fun ParentScreen(
    parentId: String,
    viewModel: ParentViewModel,
    navController: NavHostController
) {
    val children by viewModel.getChildren(parentId).collectAsState(emptyList())

    Scaffold(
        topBar = {
            ParentTopAppBar(onProfileClick = { navController.navigate("parentProfileScreen") })
        },
        backgroundColor = Color(0xFFE3F0F7),
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFB3E5FC)
            ) {
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("parentProfileScreen") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
                BottomNavigationItem(
                    selected = true,
                    onClick = { navController.navigate("parentScreen") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("addChildrenScreen") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Children") },
                    label = { Text("Add Children") }
                )
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(children) { child ->
                    ChildItem(child = child, onChildClick = { /* handle child item click */ })
                }
            }
        }
    }
}

@Composable
fun ParentTopAppBar(onProfileClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Parent Screen") },
        navigationIcon = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            }
        },
        backgroundColor = Color(0xFFB3E5FC)
    )
}

@Composable
fun ChildItem(child: Child, onChildClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onChildClick() },
        backgroundColor = Color.White,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Child Icon",
                modifier = Modifier
                    .size(64.dp)
                    .clip(shape = CircleShape)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = child.fullName,
                    style = MaterialTheme.typography.subtitle1
                )

                Text(
                    text = "tasks to confirm", /*${child.tasksToConfirm} */
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
    }
}