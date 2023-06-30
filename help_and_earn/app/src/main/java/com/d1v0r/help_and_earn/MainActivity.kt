package com.d1v0r.help_and_earn

import ParentViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d1v0r.help_and_earn.auth.LoginScreen
import com.d1v0r.help_and_earn.auth.RegisterScreen
import com.d1v0r.help_and_earn.auth.ResetPasswordScreen
import com.d1v0r.help_and_earn.auth.viewmodel.LoginViewModel
import com.d1v0r.help_and_earn.auth.viewmodel.RegisterViewModel
import com.d1v0r.help_and_earn.child.ChildScreen
import com.d1v0r.help_and_earn.parent.ParentApproveTasksScreen
import com.d1v0r.help_and_earn.parent.ParentProfileScreen
import com.d1v0r.help_and_earn.parent.ParentScreen
import com.d1v0r.help_and_earn.parent.ParentTasksScreen
import com.d1v0r.help_and_earn.ui.theme.Help_and_earnTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Help_and_earnTheme {
                MyNavHost()
            }
        }
    }
}

@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    NavHost(navController = navController, startDestination = "loginFragment") {
        composable("loginFragment") {
            LoginScreen(navController, LoginViewModel())
        }
        composable("registerFragment") {
            RegisterScreen(navController, RegisterViewModel())
        }
        composable("forgotPasswordFragment") {
            ResetPasswordScreen(navController)
        }
        composable("parentScreen") {
            val viewModel: ParentViewModel = viewModel()
            val parentId = currentUser?.uid ?: ""
            ParentScreen(parentId = parentId, viewModel = viewModel, navController)
        }
        composable("childScreen") {
            ChildScreen()
        }
        composable("parentProfileScreen") { ParentProfileScreen(onDeleteProfile = { /* handle delete profile */ }) }
    }
}

@Composable
fun ParentNavHost(navController: NavHostController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    NavHost(navController = navController, startDestination = "parentScreen") {
        composable("parentApproveTasksScreen") { ParentApproveTasksScreen() }
        composable("parentProfileScreen") { ParentProfileScreen(onDeleteProfile = { /* handle delete profile */ }) }
        composable("parentTasksScreen") { ParentTasksScreen() }
        composable("parentScreen") {
            val viewModel: ParentViewModel = viewModel()
            val parentId = currentUser?.uid ?: ""
            ParentScreen(parentId = parentId, viewModel = viewModel, navController)
        }
    }
}

