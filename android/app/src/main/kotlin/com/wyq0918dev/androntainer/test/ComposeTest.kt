package com.wyq0918dev.androntainer.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wyq0918dev.androntainer.ui.layout.Greeting
import com.wyq0918dev.androntainer.ui.theme.AndrontainerTheme

class ComposeTest : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndrontainerTheme {
                Greeting(name = "sb")
            }
        }
    }
}