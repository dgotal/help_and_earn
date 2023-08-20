package com.d1v0r.help_and_earn.parent

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.parent.viewmodel.ParentViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ParentProfileScreen(
    navController: NavHostController
) {
    val viewModel: ParentViewModel = hiltViewModel()
    val parent by viewModel.parent.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }

    var selectedAvatarUrl by remember { mutableStateOf(parent.imagePath) }

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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Profile", color = Color.White, fontSize = 22.sp)
                },
                backgroundColor = Color(0xFF995825)
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFB3E5FC)
            ) {
                BottomBar(navController)
            }
        }
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
                contentAlignment = Alignment.Center,
            ) {
                AvatarItem(
                    isSelected = avatarSelectionVisible,
                    onAvatarClick = { avatarSelectionVisible = true },
                    avatarUrl = parent.imagePath,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileInfoRow("Full Name", parent.fullName)
                ProfileInfoRow("Email", parent.email)
                ProfileInfoRow("Username", parent.username)
            }
            Button(
                onClick = {
                    showLogoutDialog.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF995825))
            ) {
                Text("Logout", color = Color.White)
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
    }

    if (avatarSelectionVisible) {
        AvatarSelectionModal(
            avatars = avatars,
            selectedAvatarUrl = selectedAvatarUrl,
            onAvatarSelected = { newAvatarUrl ->
                selectedAvatarUrl = newAvatarUrl
                viewModel.saveAvatarParent(newAvatarUrl)
                avatarSelectionVisible = false
                parent.imagePath = newAvatarUrl
            },
            onCancel = { avatarSelectionVisible = false }
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xC3995825),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}