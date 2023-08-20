package com.d1v0r.help_and_earn.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.parent.viewmodel.ParentViewModel

@Composable
fun ParentScreen(
    navController: NavHostController,
) {
    val viewModel: ParentViewModel = hiltViewModel()
    val children by viewModel.getChildren().collectAsState(emptyList())
    Scaffold(
        topBar = {
            ParentTopAppBar(
                onActionClick = {
                    navController.navigate("addChildrenScreen")
                })
        },
        backgroundColor = Color.White,
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFFFFFFF)
            ) {
                BottomBar(navController)
            }
        })
    { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(children) { child ->
                ChildItem(child = child, onChildClick = {
                    navController.navigate("parentChildClickedScreen/${child.id}")
                })
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        BottomNavigationItem(
            selected = currentDestination?.hierarchy?.any { it.route == "parentScreen" } == true,
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
        BottomNavigationItem(
            selected = currentDestination?.hierarchy?.any { it.route == "parentProfileScreen" } == true,
            onClick = {
                navController.navigate("parentProfileScreen") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selectedContentColor = Color(0xFF814A1E)
        )
    }
}

@Composable
fun ParentTopAppBar(
    onActionClick: () -> Unit
) {
    val viewModel: ParentViewModel = hiltViewModel()
    val child by viewModel.child.collectAsState()
    val childName = if (child != null) "${child?.fullName} Tasks" else "Children"

    TopAppBar(
        title = { Text(text = childName, color = Color.White, fontSize = 22.sp) },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(Icons.Default.Add, contentDescription = "Action")
            }
        }, backgroundColor = Color(0xFF995825), contentColor = Color.White
    )
}

@Composable
fun ChildItem(child: Child, onChildClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { onChildClick() },
        backgroundColor = Color.White,
        elevation = 6.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = child.imagePath,
                contentDescription = "Child profile screen",
                modifier = Modifier
                    .size(64.dp)
                    .clip(shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = child.fullName,
                    style = MaterialTheme.typography.subtitle1
                )

                Text(
                    text = "Click here to check tasks",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
    }
}