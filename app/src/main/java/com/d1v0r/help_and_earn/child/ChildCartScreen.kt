package com.d1v0r.help_and_earn.child

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.model.Wishlist

@Composable
fun ChildCartScreen(
    navController: NavHostController,
) {
    val viewModel: ChildViewModel = hiltViewModel()
    val wishlistItems: List<Wishlist> by viewModel.wishlistItems.collectAsState()

    val tabTitles = listOf("Available", "Bought")
    val selectedTabIndex = rememberTabState()

    val child by viewModel.child.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Cart",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                        Text("Balance: ${child.balance}", color = Color.White, fontSize = 22.sp)
                    }
                },
                backgroundColor = Color(0xFF995825)
            )
        },
        backgroundColor = Color.White,
        bottomBar = {
            ChildBottomBar(navController)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
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
                        text = { Text(title) }
                    )
                }
            }
            AnimatedContent(
                targetState = selectedTabIndex,
                label = ""
            ) { targetState ->
                val items = when (targetState.value) {
                    0 -> {
                        wishlistItems.filter { !it.bought }
                    }

                    else -> {
                        wishlistItems.filter { it.bought }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(items) { wishlistItem ->
                        if (wishlistItem.bought) {
                            WishlistBoughtItem(wishlistItem)
                        } else
                            WishlistActiveItem(
                                child,
                                wishlistItem
                            ) {
                                viewModel.buyWishlistItem(wishlistItem)
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberTabState(): MutableState<Int> {
    return remember { mutableIntStateOf(0) }
}

@Composable
fun WishlistActiveItem(child: Child, item: Wishlist, onBuyClick: () -> Unit) {
    val showWarningDialog = remember { mutableStateOf(false) }
    val showConfirmDialog = remember { mutableStateOf(false) }

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
                    onClick = {
                        if (child.balance >= item.price) {
                            showConfirmDialog.value = true
                        } else {
                            showWarningDialog.value = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Buy", color = Color.White)
                }
            }
        }

        if (showWarningDialog.value) {
            AlertDialog(
                onDismissRequest = { showWarningDialog.value = false },
                title = { Text("Insufficient Balance") },
                text = { Text("You don't have enough balance to buy this item.") },
                confirmButton = {
                    Button(
                        onClick = { showWarningDialog.value = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                0xFF995825
                            )
                        )
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }

        if (showConfirmDialog.value) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog.value = false },
                title = { Text("Confirm Purchase") },
                text = { Text("Do you want to buy this item for ${item.price} coins?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog.value = false
                            onBuyClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                0xFF995825
                            )
                        )
                    ) {
                        Text("Buy", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmDialog.value = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                0xFF801B37
                            )
                        )
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun WishlistBoughtItem(item: Wishlist) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        backgroundColor = Color.White,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row {
                Text(
                    item.itemName,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

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
    }
}
