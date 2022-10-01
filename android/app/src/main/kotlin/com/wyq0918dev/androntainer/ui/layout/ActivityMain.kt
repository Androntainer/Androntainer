package com.wyq0918dev.androntainer.ui.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wyq0918dev.androntainer.ui.theme.AndrontainerTheme

@Composable
fun Greeting(name: String) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "sb")
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = null)
                    }
                },
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colors.background
        ) {
            Text(text = "Hello $name!")
            // WidgetFlutter()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndrontainerTheme {
        Greeting("Android")
    }
}