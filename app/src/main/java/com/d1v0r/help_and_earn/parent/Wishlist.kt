package com.d1v0r.help_and_earn.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.model.Wishlist
import com.d1v0r.help_and_earn.parent.viewmodel.WishlistViewModel

@Composable
fun Wishlist(navController: NavHostController, childId: String) {
    val viewModel: WishlistViewModel = hiltViewModel()
    viewModel.loadWishlist(childId)

    val childWishlist by viewModel.wishlistItems.collectAsState()
    val child by viewModel.child.collectAsState()

    var selectedTab by remember { mutableStateOf(WishlistTab.Active) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Wishlist - ${child?.fullName}")
                },
                actions = {
                    IconButton(onClick = { navController.navigate("addWish/$childId") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Wish")
                    }
                }, backgroundColor = Color(0xFF995825), contentColor = Color.White
            )
        },
        backgroundColor = Color.White,
        bottomBar = {
            ParentChildClickedBottomBar(navController, childId)
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x11864733),
                contentColor = Color(0xFF804313)
            ) {
                WishlistTab.values().forEachIndexed { _, tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) }
                    )
                }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    when (selectedTab) {
                        WishlistTab.Active -> childWishlist.filter { !it.bought }
                        WishlistTab.Bought -> childWishlist.filter { it.bought }
                    }
                ) { item ->
                    if (item.bought) {
                        WishlistBoughtItem(
                            item,
                            onDelete = { viewModel.deleteWishlistItem(item) },
                            onEdit = { viewModel.updateWishlistItem(item) })
                    } else {
                        WishlistActiveItem(
                            item,
                            onDelete = { viewModel.deleteWishlistItem(item) },
                            onEdit = { viewModel.updateWishlistItem(item) })
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistActiveItem(item: Wishlist, onEdit: () -> Unit, onDelete: () -> Unit) {
    val viewModel: WishlistViewModel = hiltViewModel()
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color.White,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row {
                Text(item.itemName, fontSize = 22.sp, modifier = Modifier.padding(top = 4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("${item.price} ", modifier = Modifier.padding(top = 4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.coin_icon),
                        contentDescription = "Coin",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Delete", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showUpdateDialog = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Edit", color = Color.White)
                }
            }
        }
        if (showUpdateDialog) {
            UpdateWishlistItemDialog(
                initialItemName = item.itemName,
                initialPrice = item.price,
                onConfirm = { newItemName, newPrice ->
                    viewModel.updateWishlistItem(
                        Wishlist(
                            item.id,
                            item.childId,
                            newItemName,
                            newPrice.toInt(),
                            item.parentId,
                            item.bought
                        )
                    )
                },
                onDismiss = {
                    showUpdateDialog = false
                }
            )
        }
        if (showDeleteConfirmation) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.deleteWishlistItem(item)
                    showDeleteConfirmation = false
                },
                onDismiss = {
                    showDeleteConfirmation = false
                }
            )
        }
    }
}


@Composable
fun WishlistBoughtItem(item: Wishlist, onEdit: () -> Unit, onDelete: () -> Unit) {
    val viewModel: WishlistViewModel = hiltViewModel()
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color.White,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row {
                Text(item.itemName, fontSize = 22.sp, modifier = Modifier.padding(top = 4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("${item.price} ", modifier = Modifier.padding(top = 4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.coin_icon),
                        contentDescription = "Coin",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        if (showUpdateDialog) {
            UpdateWishlistItemDialog(
                initialItemName = item.itemName,
                initialPrice = item.price,
                onConfirm = { newItemName, newPrice ->
                    viewModel.updateWishlistItem(
                        Wishlist(
                            item.id,
                            item.childId,
                            newItemName,
                            newPrice.toInt(),
                            item.parentId,
                            item.bought
                        )
                    )
                },
                onDismiss = {
                    showUpdateDialog = false
                }
            )
        }
        if (showDeleteConfirmation) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.deleteWishlistItem(item)
                    showDeleteConfirmation = false
                },
                onDismiss = {
                    showDeleteConfirmation = false
                }
            )
        }
    }
}

@Composable
fun UpdateWishlistItemDialog(
    initialItemName: String,
    initialPrice: Int,
    onConfirm: (newItemName: String, newPrice: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newItemName by remember { mutableStateOf(initialItemName) }
    var newPrice by remember { mutableStateOf(initialPrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit wishlist Item",
                color = Color(0xFF995825),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF995825),
                        focusedLabelColor = Color(0xFF995825),
                        cursorColor = Color(0xFF995825)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("Price") },
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
                    onConfirm(newItemName, newPrice)
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

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Deletion",
                color = Color(0xFF995825),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this item?",
                color = Color(0xFF804313),
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825))
            ) {
                Text("Delete", color = Color.White)
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

enum class WishlistTab(val title: String) {
    Active("Active"),
    Bought("Bought")
}