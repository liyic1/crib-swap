package com.example.cribswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cribswap.ui.theme.CribSwapTheme

import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cribswap.ui.theme.Pink40
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CribSwapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview
@Composable
fun FilterButton() {
    Button( onClick = { }) {
        Text("Filter")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CribSwapTheme {
        Greeting("Android")
    }
}

@Composable
fun FilterDrawer() {
    val navigationController = rememberNavController()
    // open/closing drawer
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current.applicationContext

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent= {
            ModalDrawerSheet {
                Box(modifier = Modifier
                    .background(Pink40)
                    .fillMaxWidth()
                    .height(150.dp)
                ) {
                    Text(text = "") // Header
                }
                // Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Price Range", color = Pink40) },
                    selected = false,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "home", tint = Pink40) },
                    onClick = {
                        coroutineScope.launch{
                            drawerState.close()
                        }
//                        navigationController.navigate(Screens.Home) { popUpTo(0)} should return to "for you page" when clicked
                    }
                )

            }

        }) {

    }
}