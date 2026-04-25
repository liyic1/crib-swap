package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cribswap.R
import com.example.cribswap.data.SignUpUIEvent
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxSize().padding(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // scrollable in case of small screens
                verticalArrangement = Arrangement.Center
            ) {
                NormalTextComponent("Sign Up")
                NormalTextComponent("Join our verified student community")
                Spacer(modifier = Modifier.height(16.dp))

                // First Name
                MyTextField(
                    labelValue = "First Name",
                    painterResource = painterResource(id = R.drawable.person),
                    onTextSelected = { signUpViewModel.onEvent(SignUpUIEvent.FirstNameChanged(it)) },
                    errorStatus = signUpViewModel.registrationUIState.value.firstNameError
                )

                // Last Name
                MyTextField(
                    labelValue = "Last Name",
                    painterResource = painterResource(id = R.drawable.person),
                    onTextSelected = { signUpViewModel.onEvent(SignUpUIEvent.LastNameChanged(it)) },
                    errorStatus = signUpViewModel.registrationUIState.value.lastNameError
                )

                // Email
                MyTextField(
                    labelValue = "School Email",
                    painterResource = painterResource(id = R.drawable.email),
                    onTextSelected = { signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it)) },
                    errorStatus = signUpViewModel.registrationUIState.value.emailError
                )

                // Password
                PasswordTextFieldComponent(
                    onTextSelected = { signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it)) },
                    labelValue = "Password",
                    painterResource = painterResource(id = R.drawable.lock),
                    errorStatus = signUpViewModel.registrationUIState.value.passwordError
                )

                // Re-enter Password
                ReEnterPasswordTextFieldComponent(
                    onTextSelected = { signUpViewModel.onEvent(SignUpUIEvent.ReEnterPasswordChanged(it)) },
                    labelValue = "Re-enter Password",
                    painterResource = painterResource(id = R.drawable.lock),
                    errorStatus = signUpViewModel.registrationUIState.value.passwordError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show error if sign up fails
                signUpViewModel.signUpError.value?.let { error ->
                    Text(text = error, color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                ButtonComponent(
                    value = "Verify",
                    onButtonClicked = {
                        signUpViewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                    },
                    isEnabled = signUpViewModel.allValidatorPassed.value
                )

                Spacer(modifier = Modifier.height(24.dp))
                DividerTextComponent()
                ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                    CribSwapAppRouter.navigateTo(Screen.LoginScreen)
                })
            }
        }

        if (signUpViewModel.signUpInProgress.value) {
            CircularProgressIndicator()
        }
    }
}
