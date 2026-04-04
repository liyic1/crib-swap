package com.example.cribswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CribSwapApp()
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    CribSwapTheme {
//        Greeting("Android")
//    }
//}