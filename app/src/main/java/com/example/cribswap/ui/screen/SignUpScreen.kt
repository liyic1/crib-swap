package com.example.cribswap.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.R
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.example.cribswap.ui.components.ButtonComponent
import com.example.cribswap.ui.components.ClickableLoginTextComponent
import com.example.cribswap.ui.components.DividerTextComponent
import com.example.cribswap.ui.components.MyTextField
import com.example.cribswap.ui.components.NormalTextComponent
import com.example.cribswap.ui.components.PasswordTextFieldComponent
import com.example.cribswap.ui.components.ReEnterPasswordTextFieldComponent
import com.example.cribswap.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel = viewModel()) {
    // Use androidx lifecycle viewModel(...)
//    val loginViewModel = LoginViewModel()

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize().padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            NormalTextComponent("Sign Up")
            NormalTextComponent("Join our verified student community")
            Spacer(modifier = Modifier.height(16.dp))
            MyTextField(
                textValue = signUpViewModel.email,
                onValueChanged = { signUpViewModel.email = it },
                labelValue = "School Email",
                painterResource = painterResource(id= R.drawable.email),
            )
            PasswordTextFieldComponent(
                textValue = signUpViewModel.password,
                onValueChanged = { signUpViewModel.password = it },
                labelValue = "Password",
                painterResource = painterResource(id= R.drawable.lock)
            )
            ReEnterPasswordTextFieldComponent(
                textValue = signUpViewModel.reEnteredPassword,
                onValueChanged = { signUpViewModel.reEnteredPassword = it },
                labelValue = "Ren-enter Password",
                painterResource = painterResource(id= R.drawable.lock)
            )
            Spacer(modifier = Modifier.height(40.dp))

            ButtonComponent("Verify")
            Spacer(modifier = Modifier.height(24.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                CribSwapAppRouter.navigateTo(Screen.LoginScreen)
            })
        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen() {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = CribSwapAppRouter.currentScreen) { currentState ->
            when (currentState.value) {
                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }
                is Screen.LoginScreen -> {
                LoginScreen()
                }
            }

        }
    }
}