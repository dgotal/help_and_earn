package com.d1v0r.help_and_earn.child

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.parent.AvatarItem
import com.d1v0r.help_and_earn.parent.AvatarSelectionModal
import com.d1v0r.help_and_earn.parent.ProfileInfoRow

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChildProfileScreen(navController: NavHostController) {
    val viewModel: ChildViewModel = hiltViewModel()
    val child by viewModel.child.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }

    val avatars = listOf(
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar1.png?alt=media&token=526a63cc-0187-4b23-802e-30e37e0d9b32",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar2.png?alt=media&token=9bc302ba-02a3-43aa-a2a6-33493cfa1173",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar3.png?alt=media&token=4270a594-16e3-4578-a4e1-5c6630264c60",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar4.png?alt=media&token=f98ea1e3-455d-4188-88e2-c1e2a9e7db2e",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar6.png?alt=media&token=96314d0c-4a0c-4b1a-83f7-bcf482ccc692",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar5.png?alt=media&token=275067d9-9161-4154-9b57-aa935fd8e064",
        "https://firebasestorage.googleapis.com/v0/b/helpandearn-c6485.appspot.com/o/images%2Favatar7.jpg?alt=media&token=eeea4e6f-01b0-44ec-9d9f-43ee5f9ec228"
    )

    var avatarSelectionVisible by remember { mutableStateOf(false) }
    var selectedAvatarUrl by remember { mutableStateOf(child.imagePath) }
    val balance = child.balance

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile", color = Color.White, fontSize = 22.sp) },
                backgroundColor = Color(0xFF995825)
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFB3E5FC)
            ) {
                ChildBottomBar(navController)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .background(Color(0x63ECD2B7)),
                contentAlignment = Alignment.Center
            ) {
                AvatarItem(
                    isSelected = avatarSelectionVisible,
                    onAvatarClick = { avatarSelectionVisible = true },
                    avatarUrl = viewModel.getChildImagePath(),
                )
            }
            BalanceSection(balance = balance)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileInfoRow("Full Name", child.fullName)
                ProfileInfoRow("Username", child.username)
            }
            Button(
                onClick = {
                    showLogoutDialog.value = true
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(76.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825)),
            ) {
                Text("Logout", color = Color.White, fontSize = 16.sp)
            }
        }
        if (showLogoutDialog.value) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog.value = false },
                title = { androidx.compose.material.Text("Logout") },
                text = { androidx.compose.material.Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearUserData()
                            navController.navigate("loginFragment")
                            showLogoutDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825))
                    ) {
                        androidx.compose.material.Text("Confirm", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showLogoutDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF801B37))
                    ) {
                        androidx.compose.material.Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }

    if (avatarSelectionVisible) {
        AvatarSelectionModal(
            avatars = avatars,
            selectedAvatarUrl = selectedAvatarUrl,
            onAvatarSelected = { newAvatarUrl ->
                selectedAvatarUrl = newAvatarUrl
                viewModel.saveAvatar(newAvatarUrl)
                avatarSelectionVisible = false
                child.imagePath = selectedAvatarUrl
            },
            onCancel = { avatarSelectionVisible = false }
        )
    }
}

@Composable
fun BalanceSection(balance: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = Color(0xFF995825),
        elevation = 6.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Your Balance:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontSize = 18.sp,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "$balance ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                )
                Image(
                    painter = painterResource(id = R.drawable.coin_icon),
                    contentDescription = "Coin",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}