package com.example.cribswap.ui.screen

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
import com.example.cribswap.ui.components.ButtonComponent
import com.example.cribswap.ui.components.MyTextField
import com.example.cribswap.ui.components.NormalTextComponent
import com.example.cribswap.ui.components.PasswordTextFieldComponent
import com.example.cribswap.viewmodel.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
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
            NormalTextComponent("Welcome Back")
            NormalTextComponent("Hello Again!")
            Spacer(modifier = Modifier.height(16.dp))
            MyTextField(
                textValue = loginViewModel.email,
                onValueChanged = { loginViewModel.email = it },
                labelValue = "School Email",
                painterResource = painterResource(id= R.drawable.email),
            )
            PasswordTextFieldComponent(
                textValue = loginViewModel.password,
                onValueChanged = { loginViewModel.password = it },
                labelValue = "Password",
                painterResource = painterResource(id= R.drawable.lock)
            )
            Spacer(modifier = Modifier.height(24.dp))

            ButtonComponent("Login")
        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfLoginScreen() {
    LoginScreen()
}
