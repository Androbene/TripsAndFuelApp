package ua.androbene.tripsandfuel.user_interface.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ua.androbene.tripsandfuel.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_prefs, rootKey)
    }
}