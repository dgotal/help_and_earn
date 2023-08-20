package com.d1v0r.help_and_earn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d1v0r.help_and_earn.auth.LoginScreen
import com.d1v0r.help_and_earn.auth.RegisterScreen
import com.d1v0r.help_and_earn.auth.ResetPasswordScreen
import com.d1v0r.help_and_earn.child.ChildCartScreen
import com.d1v0r.help_and_earn.child.ChildProfileScreen
import com.d1v0r.help_and_earn.child.ChildScreen
import com.d1v0r.help_and_earn.parent.AddChild
import com.d1v0r.help_and_earn.parent.AddTask
import com.d1v0r.help_and_earn.parent.AddWish
import com.d1v0r.help_and_earn.parent.ParentChildClickedScreen
import com.d1v0r.help_and_earn.parent.ParentProfileScreen
import com.d1v0r.help_and_earn.parent.ParentScreen
import com.d1v0r.help_and_earn.parent.Wishlist
import com.d1v0r.help_and_earn.ui.theme.Help_and_earnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
    val startDestination = getStartDestination()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("loginFragment") {
            LoginScreen(navController)
        }
        composable("registerFragment") {
            RegisterScreen(navController)
        }
        composable("forgotPasswordFragment") {
            ResetPasswordScreen(navController)
        }
        composable("parentScreen") {
            ParentScreen(navController)
        }
        composable("childScreen") {
            ChildScreen(navController)
        }
        composable("parentProfileScreen") {
            ParentProfileScreen(
                navController
            )
        }
        composable("parentChildClickedScreen/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            ParentChildClickedScreen(navController = navController, childId = childId)
        }
        composable("addTask/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            AddTask(navController = navController, childId)
        }
        composable("addChildrenScreen") {
            AddChild(navController)
        }
        composable("wishlist/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            Wishlist(navController = navController, childId = childId)
        }
        composable("childProfileScreen") {
            ChildProfileScreen(navController)
        }
        composable("childCartScreen") {
            ChildCartScreen(navController)
        }
        composable("addWish/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            AddWish(navController = navController, childId = childId)
        }
    }
}

@Composable
fun getStartDestination(): String {
    val viewModel: MainActivityViewModel = hiltViewModel()
    return when {
        viewModel.isAuthenticated && viewModel.isParent -> {
            "parentScreen"
        }

        viewModel.isAuthenticated -> {
            "childScreen"
        }

        else -> {
            "loginFragment"
        }
    }
}
