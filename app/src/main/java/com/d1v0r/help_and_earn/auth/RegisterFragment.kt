package com.d1v0r.help_and_earn.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.auth.viewmodel.RegisterViewModel
import com.d1v0r.help_and_earn.model.Parent


@Composable
fun RegisterScreen(navController: NavController) {
    val registerViewModel: RegisterViewModel = hiltViewModel()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Parent") }

    val registrationSuccess by registerViewModel.registrationSuccess.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess == true) {
            Toast.makeText(context, "User created", Toast.LENGTH_SHORT).show()
            navController.navigate("loginFragment")
        } else if (registrationSuccess == false) {
            Toast.makeText(context, "User non created", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            RegisterHeader()

            RegisterFields(
                username = username,
                fullName = fullName,
                email = email,
                password = password,
                role = role,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                onFullNameChange = { fullName = it },
                onEmailChange = { email = it },
                onRoleChange = { role = it }
            )
            RegisterFooter(
                onRegisterClick = {
                    val user = Parent(
                        username = username,
                        fullName = fullName,
                        role = role,
                        email = email,
                        password = password
                    )
                    registerViewModel.registerUser(user)
                },
                onIAmAlreadyAMemberClick = { navController.navigate("loginFragment") }
            )
        }
    }
}

@Composable
fun RegisterHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Hello There!", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "Sign up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun RegisterFields(
    username: String,
    fullName: String,
    email: String,
    password: String,
    role: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
) {
    Column {
        InputField(
            value = username,
            label = "Username",
            placeholder = "Enter your email address",
            onValueChange = onUsernameChange,
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(8.dp))
        InputField(
            value = fullName,
            label = "Full name",
            placeholder = "Enter your full name",
            onValueChange = onFullNameChange,
            leadingIcon = {
                Icon(Icons.Default.Badge, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(8.dp))
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
        Spacer(Modifier.height(8.dp))
        InputField(
            value = password,
            label = "Password",
            placeholder = "Enter your password",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            )
        )
    }
}


@Composable
fun RegisterFooter(
    onRegisterClick: () -> Unit,
    onIAmAlreadyAMemberClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Register")
        }
        TextButton(onClick = {
            onIAmAlreadyAMemberClick()
        }) {
            Text(text = "I'm already a member")
        }
    }
}