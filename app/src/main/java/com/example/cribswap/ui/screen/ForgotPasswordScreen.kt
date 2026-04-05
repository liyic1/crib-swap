package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.R
import com.example.cribswap.data.LoginUIEvent
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.components.BackButton
import com.example.cribswap.ui.components.ButtonComponent
import com.example.cribswap.ui.components.MyTextField
import com.example.cribswap.ui.components.NormalTextComponent
import com.example.cribswap.viewmodel.LoginViewModel

@Composable
fun ForgotPasswordScreen(loginViewModel: LoginViewModel = viewModel()) {
    // Use androidx lifecycle viewModel(...)

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
        ) {
            BackButton {
                CribSwapAppRouter.navigateTo(Screen.LoginScreen)
            }
            NormalTextComponent("Forgot Password")
            NormalTextComponent("Please enter your email to reset the password")
            MyTextField(
                onTextSelected = {  loginViewModel.onEvent(LoginUIEvent.EmailChanged(it)) },
                labelValue = "Enter your email",
                painterResource = painterResource(id= R.drawable.email),
            )
            Spacer(modifier = Modifier.height(30.dp))

            ButtonComponent(
                value = "Reset Password",
                onButtonClicked = {
                loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                },
                isEnabled = loginViewModel.allValidatorPassed.value)
        }
    }
}
