package com.sydev.vpnsyria.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.VPNApp
import com.sydev.vpnsyria.utilties.Countries
import com.sydev.vpnsyria.utilties.ServerPreferences






class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            val preference = findPreference("$key") as Preference?
            if (key=="selectedCountry"){
                preference!!.summary = (preference as ListPreference).entry
            }
        }
        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val serverRepo = VPNApp.getAppContext().getServerRepository()
            val countryList = serverRepo.getUniqueCountries()
            val entriesValues = arrayOfNulls<CharSequence>(countryList.size)
            val entries = arrayOfNulls<CharSequence>(countryList.size)

            for (i in countryList.indices) {
                entriesValues[i] = countryList[i].countryLong
                val localeCountryName =
                    if (Countries.getCountries()[countryList[i].countryShort] != null)
                        Countries.getCountries()[countryList[i].countryShort]
                    else
                        countryList[i].countryLong
                entries[i] = localeCountryName
            }

            val listPreference = findPreference("selectedCountry") as ListPreference?
            if (entries.isEmpty()) {
                val countryPriorityCategory = findPreference("countryPriorityCategory") as PreferenceCategory?
                preferenceScreen.removePreference(countryPriorityCategory)
            } else {
                listPreference!!.entries = entries
                listPreference.entryValues = entriesValues
                if (ServerPreferences.getSelectedCountry() == null)
                    listPreference.setValueIndex(0)
                else
                    listPreference.summary = ServerPreferences.getSelectedCountry()
            }


        }

    }
}