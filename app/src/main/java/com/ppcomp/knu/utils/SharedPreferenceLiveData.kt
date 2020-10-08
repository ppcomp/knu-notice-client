package com.ppcomp.knu.utils

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.lifecycle.LiveData

/**
 * SharedPreference 의 변화감지를 위한 LiveData
 * @author 정준
 */
abstract class SharedPreferenceLiveData<T> constructor(
    protected val prefs: SharedPreferences,
    private val key: String,
    private val defValue: T) : LiveData<T>(){

    private val preferenceChangeListener =
        OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key,defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key,defValue)
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class SharedPreferenceIntLiveData(prefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(prefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int = prefs.getInt(key, defValue)
}

class SharedPreferenceStringLiveData(prefs: SharedPreferences, key: String, defValue: String) :
    SharedPreferenceLiveData<String>(prefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String): String = prefs.getString(key, defValue)!!
}

class SharedPreferenceBooleanLiveData(prefs: SharedPreferences, key: String, defValue: Boolean) :
    SharedPreferenceLiveData<Boolean>(prefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = prefs.getBoolean(key, defValue)
}
