package com.sydev.vpnsyria.utilties

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sydev.vpnsyria.VPNApp

class ServerPreferences {
companion object{
    private var prefs: SharedPreferences? = null
    private const val DOWNLOADED_DATA_KEY = "downloaded_data"
    private const val UPLOADED_DATA_KEY = "uploaded_data"
    private const val AUTOMATIC_SWITCHING = "autoSwitch"
    private const val COUNTRY_PRIORITY = "countryPriority"
    private const val CONNECT_ON_START = "connectOnStart"
    private const val AUTOMATIC_SWITCHING_SECONDS = "automaticSwitchingSeconds"
    private const val SELECTED_COUNTRY = "selectedCountry"
    private const val AUTO_UPDATE = "update_servers"
    private const val EXPIRE_DATE ="ExpiredDate"
    @Synchronized
    private fun getPrefs(): SharedPreferences? {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(VPNApp.getAppContext())
        }
        return prefs
    }

    fun getDownloaded(): Long {
        return getPrefs()!!.getLong(DOWNLOADED_DATA_KEY, 0)
    }

    fun setDownloaded(count: Long) {
        getPrefs()!!.edit().putLong(DOWNLOADED_DATA_KEY, count).apply()
    }

    fun getUploaded(): Long {
        return getPrefs()!!.getLong(UPLOADED_DATA_KEY, 0)
    }

    fun setUploaded(count: Long) {
        getPrefs()!!.edit().putLong(UPLOADED_DATA_KEY, count).apply()
    }

    fun getConnectOnStart(): Boolean {
        return getPrefs()!!.getBoolean(CONNECT_ON_START, false)
    }

    fun getAutomaticSwitching(): Boolean {
        return getPrefs()!!.getBoolean(AUTOMATIC_SWITCHING, true)
    }

    fun getAutomaticSwitchingSeconds(): Long {
        return getPrefs()!!.getLong(AUTOMATIC_SWITCHING_SECONDS, 20)
    }

    fun getCountryPriority(): Boolean {
        return getPrefs()!!.getBoolean(COUNTRY_PRIORITY, false)
    }

    fun getSelectedCountry(): String? {
        return getPrefs()!!.getString(SELECTED_COUNTRY, null)
    }
/*
    fun getAutoUpdateState():Boolean{
        return getPrefs()!!.getBoolean(AUTO_UPDATE,true)
    }

    fun setExpirationDate(){
        getPrefs()!!.edit().putLong(EXPIRE_DATE,System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(120)).apply()
    }

    fun isExpired():Boolean{
        return getPrefs()!!.getLong(EXPIRE_DATE,-1)>System.currentTimeMillis()
    }*/
}
}