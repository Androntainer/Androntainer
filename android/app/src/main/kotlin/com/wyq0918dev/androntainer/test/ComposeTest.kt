package com.wyq0918dev.androntainer.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wyq0918dev.androntainer.AndrontainerTheme
import com.wyq0918dev.androntainer.ui.layout.Greeting

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