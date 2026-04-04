package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.R
import com.example.cribswap.data.LoginUIEvent
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.components.ButtonComponent
import com.example.cribswap.ui.components.ClickableLoginTextComponent
import com.example.cribswap.ui.components.DividerTextComponent
import com.example.cribswap.ui.components.MyTextField
import com.example.cribswap.ui.components.NormalTextComponent
import com.example.cribswap.ui.components.PasswordTextFieldComponent
import com.example.cribswap.ui.components.UnderlinedTextComponent
import com.example.cribswap.viewmodel.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxSize().padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                NormalTextComponent("Welcome Back")
                NormalTextComponent("Hello Again!")
                Spacer(modifier = Modifier.height(16.dp))
                MyTextField(
                    onTextSelected = { loginViewModel.onEvent(LoginUIEvent.EmailChanged(it)) },
                    labelValue = "School Email",
                    painterResource = painterResource(id = R.drawable.email),
                    errorStatus = loginViewModel.loginUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    onTextSelected = { loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
                    labelValue = "Password",
                    painterResource = painterResource(id = R.drawable.lock),
                    errorStatus = loginViewModel.loginUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(8.dp))
                UnderlinedTextComponent(value = "Forgot Password?", onTextSelected = {
                    CribSwapAppRouter.navigateTo(Screen.ForgotPasswordScreen)
                })
                Spacer(modifier = Modifier.height(24.dp))

                ButtonComponent(
                    value = "Login",
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidatorPassed.value
                )
                DividerTextComponent()
                ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                    CribSwapAppRouter.navigateTo(Screen.SignUpScreen)
                })
            }
        }
        if (loginViewModel.loginInProgress.value) {
            CircularProgressIndicator()
        }
    }
}

