package com.d1v0r.help_and_earn

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("HelpAndEarn", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "USER_ID"
        const val IS_PARENT = "IS_PARENT"
        const val IS_CHILD = "IS_CHILD"
    }

    fun saveUser(userId: String?) {
        saveData(USER_ID, userId)
    }

    fun getUser(): String? {
        return getData(USER_ID, null)
    }

    fun setIsParent(isParent: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_PARENT, isParent)
        editor.apply()
    }

    fun setIsChild(isChild: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_CHILD, isChild)
        editor.apply()
    }

    fun clearUserParentData() {
        val editor = sharedPreferences.edit()
        editor.remove(USER_ID)
        editor.remove(IS_PARENT)
        editor.apply()
    }

    fun clearUserChildData() {
        val editor = sharedPreferences.edit()
        editor.remove(USER_ID)
        editor.remove(IS_CHILD)
        editor.apply()
    }

    fun getIsParent(): Boolean {
        return sharedPreferences.getBoolean(IS_PARENT, false)
    }

    fun getIsChild(): Boolean {
        return sharedPreferences.getBoolean(IS_CHILD, false)
    }

    fun saveData(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}