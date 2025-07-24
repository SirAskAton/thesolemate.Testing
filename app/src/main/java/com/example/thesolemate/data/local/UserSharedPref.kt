package com.example.thesolemate.data.local

import android.content.Context
import android.content.SharedPreferences

class UserSharedPref(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    fun saveUser(id: String, name: String, email: String, username: String) {
        pref.edit()
            .putString("user_id", id)
            .putString("name", name)
            .putString("email", email)
            .putString("username", username)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun isLoggedIn(): Boolean = pref.getBoolean("is_logged_in", false)
    fun getUserId(): String = pref.getString("user_id", "") ?: ""
    fun logout() = pref.edit().clear().apply()
}
