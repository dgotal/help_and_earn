package com.d1v0r.help_and_earn.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d1v0r.help_and_earn.R
import com.d1v0r.help_and_earn.auth.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    checkAuthenticated(navController, viewModel)

    val context = LocalContext.current

    val isParentSelected by viewModel.isParentSelected.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val showMessage by viewModel.showMessage.collectAsState()

    LaunchedEffect(showMessage) {
        if (showMessage != null) {
            Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loginSuccess) {
        when {
            loginSuccess && isParentSelected -> {
                navController.navigate("parentScreen") {
                    launchSingleTop = true
                    restoreState = true

                    popUpTo(navController.graph.id)
                    {
                        inclusive = true
                    }
                }
            }

            loginSuccess -> {
                navController.navigate("childScreen") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.id)
                    {
                        inclusive = true

                    }
                }
            }
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
        ) {
            LoginHeader()
            if (isParentSelected) {
                Spacer(modifier = Modifier.padding(top = 84.dp))
                LoginParentContent(navController = navController)
            } else {
                Spacer(modifier = Modifier.padding(top = 84.dp))
                LoginChildContent()
            }
        }
    }
}


@Composable
fun LoginHeader() {
    val viewModel: LoginViewModel = hiltViewModel()
    val enabledStyle = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color(0xFF995825)
    )
    val disabledStyle = ButtonDefaults.outlinedButtonColors(
        contentColor = Color(0xFF995825),
        containerColor = Color.White
    )
    val isParentSelected = viewModel.isParentSelected.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 48.dp)
    ) {
        Text(text = "Welcome Back", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "Sign in to continue", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
    Row() {
        Button(
            onClick = {
                viewModel.onRoleSelected(true)
            },
            shape = CutCornerShape(0.dp),
            modifier = Modifier
                .width(140.dp)
                .padding(16.dp, 64.dp, 0.dp, 16.dp),
            colors = if (isParentSelected.value) enabledStyle else disabledStyle,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(text = "Parent", textAlign = TextAlign.Center)
        }
        Button(
            onClick = {
                viewModel.onRoleSelected(false)
            },
            shape = CutCornerShape(0.dp),
            modifier = Modifier
                .width(140.dp)
                .padding(0.dp, 64.dp, 16.dp, 16.dp),
            colors = if (isParentSelected.value) disabledStyle else enabledStyle,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(text = "Child", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LoginParentContent(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    LoginFields(email, password,
        onUsernameChange = {
            email = it.replace("\n", "")
        }, onPasswordChange = {
            password = it.replace("\n", "")
        }, onForgotPasswordClick = { navController.navigate("ForgotPasswordFragment") })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            viewModel.signInWithParent(email, password)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sign in")
        }
        TextButton(onClick = { navController.navigate("registerFragment") }) {
            Text(text = "Don't have an account? Sign up")
        }
    }
}

@Composable
fun LoginChildContent() {
    val viewModel: LoginViewModel = hiltViewModel()
    var username by remember {
        mutableStateOf("")
    }
    var passcode by remember {
        mutableStateOf("")
    }
    LoginChildFields(username, passcode,
        onUsernameChange = {
            username = it.replace("\n", "")
        }, onPasswordChange = {
            passcode = it.replace("\n", "")
        })
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                viewModel.signInChild(username, passcode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 112.dp)
        ) {
            Text(text = "Sign in")
        }
    }
}


fun checkAuthenticated(navController: NavController, viewModel: LoginViewModel) {
    when {
        viewModel.isAuthenticated && viewModel.isParent -> {
            navController.navigate("parentScreen")
        }

        viewModel.isAuthenticated -> {
            navController.navigate("childScreen")
        }
    }
}

@Composable
fun LoginFields(
    email: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        InputField(
            value = email,
            label = "Email",
            placeholder = "Enter your email address",
            onValueChange = onUsernameChange,
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
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
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
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 64.dp)
        ) {
            Text(text = "Forgot Password?")
        }
    }

}

@Composable
fun LoginChildFields(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        InputField(
            value = username,
            label = "Username",
            placeholder = "Enter your username",
            onValueChange = onUsernameChange,
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Username")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(Modifier.height(8.dp))
        InputField(
            value = password,
            label = "Passcode",
            placeholder = "Enter your passcode",
            onValueChange = onPasswordChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Passcode")
            },
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
    }

}

@Composable
fun InputField(
    value: String,
    label: String,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = isSingleLine
    )
}
