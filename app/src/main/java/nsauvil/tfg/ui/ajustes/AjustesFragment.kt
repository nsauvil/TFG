package nsauvil.tfg.ui.ajustes

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import nsauvil.tfg.R

//Fragmento con el cambio al modo día y al modo noche
class AjustesFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey) //Asigna el fragmento XML de preferencias "preferences_settings"

        val switchPreference = findPreference<SwitchPreference>("night_mode")
        switchPreference?.setOnPreferenceChangeListener { _, newValue ->
            val isNightModeOn = newValue as Boolean
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
    }
}