package com.ppcomp.knu.utils

import android.content.Context
import android.content.SharedPreferences


class PreferenceHelper private constructor(context: Context) {

    companion object {

        private const val PREFERENCE_NAME = "pref"
        private lateinit var mContext: Context
        private lateinit var prefs: SharedPreferences
        private lateinit var prefsEditor: SharedPreferences.Editor
        @Volatile private var INSTANCE: PreferenceHelper? = null

        fun getInstance(context: Context): PreferenceHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceHelper(context).also {
                    INSTANCE = it
                    mContext = context
                    prefs = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                    prefsEditor = prefs!!.edit()
                }
            }

        fun get(key: String?, defValue: String?): String? {
            return prefs!!.getString(key, defValue)
        }

        fun put(key: String?, value: String?) {
            prefsEditor!!.putString(key, value)
            prefsEditor!!.commit()
        }

        fun get(key: String?, defValue: Int): Int? {
            return prefs!!.getInt(key, defValue)
        }

        fun put(key: String?, value: Int?) {
            prefsEditor!!.putInt(key, value!!).commit()
        }

        fun get(key: String?, defValue: Boolean): Boolean {
            return prefs!!.getBoolean(key, defValue)
        }

        fun put(key: String?, value: Boolean) {
            prefsEditor!!.putBoolean(key, value)
            prefsEditor!!.commit()
        }
    }
}