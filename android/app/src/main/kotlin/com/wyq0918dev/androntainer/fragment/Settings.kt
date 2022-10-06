package com.wyq0918dev.androntainer.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.wyq0918dev.androntainer.R

class Settings : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = Settings()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}