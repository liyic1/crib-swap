package com.example.cribswap.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cribswap.navigation.CribSwapAppRouter
import com.example.cribswap.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen() {
    val auth = FirebaseAuth.getInstance()

    var isVerified by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var resendCooldown by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }

    fun currentUserEmail(): String {
        return auth.currentUser?.email ?: "your email"
    }

    fun refreshVerificationStatus(
        onDone: ((Boolean) -> Unit)? = null
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onDone?.invoke(false)
            return
        }

        currentUser.reload()
            .addOnCompleteListener {
                val refreshedUser = auth.currentUser
                val verified = refreshedUser?.isEmailVerified == true
                isVerified = verified
                onDone?.invoke(verified)
            }
    }

    LaunchedEffect(Unit) {
        while (!isVerified) {
            delay(3000)
            refreshVerificationStatus()
        }
    }

    LaunchedEffect(isVerified) {
        if (isVerified) {
            message = "Email verified successfully."
            delay(1000)
            CribSwapAppRouter.navigateTo(Screen.MainScreen)
        }
    }

    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000)
            resendCooldown -= 1
        }
    }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isVerified) {
                Text(
                    text = "✅",
                    fontSize = 64.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Email Verified",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Taking you to the app...",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator()
            } else {
                Text(
                    text = "📧",
                    fontSize = 64.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verify your email",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "We sent a verification link to:\n${currentUserEmail()}",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Open the email and tap the verification link. Then come back here and continue.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isChecking = true
                        message = null
                        refreshVerificationStatus { verified ->
                            isChecking = false
                            message = if (verified) {
                                "Email verified successfully."
                            } else {
                                "Still not verified yet. Please tap the link in your email first."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isChecking
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("I've verified my email")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        val currentUser = auth.currentUser
                        if (currentUser == null) {
                            message = "No signed-in user found."
                            return@OutlinedButton
                        }

                        isResending = true
                        message = null

                        currentUser.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                isResending = false
                                if (task.isSuccessful) {
                                    resendCooldown = 30
                                    message = "Verification email resent."
                                } else {
                                    message = task.exception?.localizedMessage
                                        ?: "Failed to resend verification email."
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = resendCooldown == 0 && !isResending
                ) {
                    if (isResending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (resendCooldown > 0) {
                                "Resend in ${resendCooldown}s"
                            } else {
                                "Resend verification email"
                            }
                        )
                    }
                }

                message?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = {
                        auth.signOut()
                        CribSwapAppRouter.navigateTo(Screen.LoginScreen)
                    }
                ) {
                    Text(
                        text = "Back to Login",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}