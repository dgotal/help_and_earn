package com.d1v0r.help_and_earn.parent

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.model.Child
import com.d1v0r.help_and_earn.parent.viewmodel.ParentViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddChild(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Child", color = Color.White, fontSize = 22.sp) },
                backgroundColor = Color(0xFF995825)
            )
        },
        backgroundColor = Color(0x19995825),
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AddChildForm(navController)
        }
    }
}

@Composable
fun AddChildForm(navController: NavHostController) {
    val viewModel: ParentViewModel = hiltViewModel()
    var username by remember { mutableStateOf(TextFieldValue()) }
    var fullName by remember { mutableStateOf(TextFieldValue()) }
    var passcode by remember { mutableStateOf(TextFieldValue()) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val showMessage by viewModel.showMessage.collectAsState()
    val successCreateChild by viewModel.successCreateChild.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(showMessage) {
        if (showMessage != null) {
            Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(successCreateChild) {
        if (successCreateChild == true) {
            navController.navigate("ParentScreen")
        }
    }
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
                text = "Create your childs profile",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color(0xBF000000),
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = fullName.text,
                onValueChange = { fullName = TextFieldValue(it) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(
                        0xFF995825
                    ), focusedLabelColor = Color(0xFF995825)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Full Name", fontSize = 12.sp) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { /* Handle Next */ })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username.text,
                onValueChange = { username = TextFieldValue(it) },
                label = { Text("Username", fontSize = 12.sp) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(
                        0xFF995825
                    ), focusedLabelColor = Color(0xFF995825)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { /* Handle Next */ })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passcode.text,
                onValueChange = { passcode = TextFieldValue(it) },
                label = { Text("Passcode", fontSize = 12.sp) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(
                        0xFF995825
                    ), focusedLabelColor = Color(0xFF995825)
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                )
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddChildButton {
                    if (username.text.isBlank() || fullName.text.isBlank() || passcode.text.isBlank()) {
                        Toast.makeText(context, "Please check all fields!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        viewModel.addNewChild(
                            Child(
                                username = username.text,
                                fullName = fullName.text,
                                passcode = passcode.text
                            )
                        )
                    }
                }
            }
        }
    )
    val kidsImage: Painter = painterResource(id = R.drawable.kids)
    Image(
        painter = kidsImage, contentDescription = "Kids", modifier = Modifier.fillMaxWidth()
    )

}

@Composable
fun AddChildButton(onAddChildClick: () -> Unit) {
    Button(
        onClick = onAddChildClick,
        modifier = Modifier
            .wrapContentSize()
            .padding(24.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF995825),
            contentColor = Color.White
        )
    ) {
        Text(text = "Add Child", textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}
