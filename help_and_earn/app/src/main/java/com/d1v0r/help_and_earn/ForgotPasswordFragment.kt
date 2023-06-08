package com.d1v0r.help_and_earn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

class ForgotPasswordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent { 
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Forgotten Password Page")
                }

            }
        }
    }
}

@Composable
fun ResetPassword(navController : NavController)
{
    Column() {
        Text(text = "Reset Password Page")
        Button(onClick = { navController.navigate("loginFragment") }) {
            Text(text = "Go back")
        }
    }

}