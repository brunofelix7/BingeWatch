package me.brunofelix.bingewatch.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.brunofelix.bingewatch.R

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)

        val prefNotification = findPreference<Preference>(getString(R.string.key_notification))
        prefNotification?.onPreferenceChangeListener = this

        val prefTheme = findPreference<Preference>(getString(R.string.key_theme))
        prefTheme?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when(preference.key) {
            getString(R.string.key_notification) -> {

            }
            getString(R.string.key_theme) -> {
                activity?.recreate()
            }
        }
        return true
    }
}