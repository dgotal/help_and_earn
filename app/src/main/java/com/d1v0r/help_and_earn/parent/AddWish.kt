package com.d1v0r.help_and_earn.parent

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.d1v0r.help_and_earn.model.Wishlist
import com.d1v0r.help_and_earn.parent.viewmodel.WishlistViewModel

@Composable
fun AddWish(
    navController: NavHostController,
    childId: String
) {
    val viewModel: WishlistViewModel = hiltViewModel()
    val child = viewModel.child.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Wish for ${child.value?.fullName}",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = Color(0xFF995825)
            )
        },
        backgroundColor = Color(0x19995825),
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFFFFFFF)
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
                        text = "Create a wishlist for your child",
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xBF000000),
                        modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AddWishForm(childId, navController)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            val wishImage: Painter = painterResource(id = R.drawable.wish)
            Image(painter = wishImage, contentDescription = "Wish")
        }
    }
}

@Composable
fun AddWishForm(childId: String, navController: NavHostController) {
    val context = LocalContext.current
    val wishlistViewModel: WishlistViewModel = hiltViewModel()
    var itemName by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .background(Color(0x8BFFFFFF))
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF995825),
                focusedLabelColor = Color(0xFF995825),
                cursorColor = Color(0xFF995825)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF995825),
                focusedLabelColor = Color(0xFF995825),
                cursorColor = Color(0xFF995825)
            )
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddWishButton {
                if (itemName.text.isBlank() || price.text.isBlank()) {
                    Toast.makeText(context, "Please check all fields!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val wish = Wishlist(
                        childId = childId,
                        itemName = itemName.text,
                        price = price.text.toInt()
                    )
                    wishlistViewModel.addWish(wish)
                    navController.navigate("wishlist/$childId")
                }
            }
        }
    }
}

@Composable
fun AddWishButton(onAddWishClick: () -> Unit) {
    Button(
        onClick = onAddWishClick,
        modifier = Modifier
            .wrapContentSize()
            .padding(24.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF995825),
            contentColor = Color.White
        )
    ) {
        Text(text = "Add Wish", textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}
