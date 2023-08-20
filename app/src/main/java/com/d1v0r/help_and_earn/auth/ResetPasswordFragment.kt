package com.d1v0r.help_and_earn.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.auth.viewmodel.RegisterViewModel
import com.d1v0r.help_and_earn.auth.viewmodel.ResetPasswordViewModel


@Composable
fun ResetPasswordScreen(navController: NavController) {
    val resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
    val context = LocalContext.current
    var email by remember {
        mutableStateOf("")
    }
    val resetPasswordSuccess by resetPasswordViewModel.resetPasswordSuccess.collectAsState()

    LaunchedEffect(resetPasswordSuccess) {
        if (resetPasswordSuccess != null) {
            Toast.makeText(context, resetPasswordSuccess, Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val backgroundImage: Painter = painterResource(id = R.drawable.login_background)
        Image(
            painter = backgroundImage,
            contentDescription = "Login",
            modifier = Modifier
                .fillMaxSize()
                .blur(6.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .alpha(0.6f)
                .clip(
                    CutCornerShape(
                        topStart = 8.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 8.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background)
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            ResetPasswordHeader()
            ResetPasswordFields(email, onEmailChange = {
                email = it
            })
            ResetPasswordFooter(onResetPasswordClick = {
                resetPasswordViewModel.resetPassword(email)
            },
                onRememberPasswordClick = { navController.navigate("loginFragment") }
            )
        }
    }
}

@Composable
fun ResetPasswordHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Reset Password", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "Enter your email", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ResetPasswordFields(
    email: String,
    onEmailChange: (String) -> Unit,
) {
    Column {
        InputField(
            value = email,
            label = "Email",
            placeholder = "Enter your email",
            onValueChange = onEmailChange,
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
fun ResetPasswordFooter(
    onResetPasswordClick: () -> Unit,
    onRememberPasswordClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onResetPasswordClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Reset Password")
        }
        TextButton(onClick = {
            onRememberPasswordClick()
        }) {
            Text(text = "Remember your password, click here")
        }
    }
}