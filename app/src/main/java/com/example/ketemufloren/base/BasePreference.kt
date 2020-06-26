package com.example.ketemufloren.base

import android.content.SharedPreferences

abstract class BasePreference {
    fun putData(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun putData(key: String?, value: Int?) {
        editor.putInt(key, value!!).apply()
    }

    fun putData(key: String?, value: Boolean?) {
        editor.putBoolean(key, value!!).apply()
    }

    fun putData(key: String?, value: Long?) {
        editor.putLong(key, value!!).apply()
    }

    fun getStringData(key: String?, defValue: String?): String? {
        return sharedPreference.getString(
            key,
            defValue
        )
    }

    fun getIntegerData(key: String?, defValue: Int): Int {
        return sharedPreference.getInt(key, defValue)
    }

    fun getLongValue(key: String?, defValue: Long): Long {
        return sharedPreference.getLong(key, defValue)
    }

    fun getBooleanData(key: String?, defValue: Boolean): Boolean {
        return sharedPreference.getBoolean(key, defValue)
    }

    fun removeData(key: String?) {
        editor.remove(key).apply()
    }

    fun containData(key: String?): Boolean {
        return sharedPreference.contains(key)
    }

    private val editor: SharedPreferences.Editor
        get() = sharedPreference.edit()

    protected abstract val sharedPreference: SharedPreferences
}
