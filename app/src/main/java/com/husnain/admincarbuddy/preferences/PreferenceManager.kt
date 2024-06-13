package com.husnain.admincarbuddy.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.transition.Visibility
import com.google.gson.Gson
import com.husnain.admincarbuddy.data.ModelUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_USER = "userdata"
        private const val KEY_INCOME_VISIBILITY = "incomeVisibility"
    }


    private val myPref: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    fun saveUserData(user: ModelUser) {
        val json = gson.toJson(user)
        myPref.edit().putString(KEY_USER, json).apply()
    }

    fun getUserData(): ModelUser? {
        val json = myPref.getString(KEY_USER, null)
        return gson.fromJson(json, ModelUser::class.java)
    }

    fun setIsIncomeVisible(visibility: Boolean) {
        myPref.edit()
            .putBoolean(KEY_INCOME_VISIBILITY, visibility)
            .apply()
    }

    fun getIsIncomeVisible():Boolean{
        return myPref.getBoolean(KEY_INCOME_VISIBILITY,true)
    }

}