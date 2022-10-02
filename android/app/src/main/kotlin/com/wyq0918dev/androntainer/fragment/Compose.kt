package com.wyq0918dev.androntainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme
import com.wyq0918dev.androntainer.DisplayActivity.Companion.ComposeLayout
import com.wyq0918dev.androntainer.ui.values.composeId

class Compose : Fragment() {

    private lateinit var composeView: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeView = ComposeView(
            requireContext()
        ).apply {
            id = composeId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView {
        super.onCreateView(inflater, container, savedInstanceState)
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MdcTheme {
                ComposeLayout()
            }
        }
    }
}