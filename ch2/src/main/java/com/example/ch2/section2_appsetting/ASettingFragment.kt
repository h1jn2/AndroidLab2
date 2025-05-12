package com.example.ch2.section2_appsetting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.ch2.R

class ASettingFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_a, rootKey)
    }
}