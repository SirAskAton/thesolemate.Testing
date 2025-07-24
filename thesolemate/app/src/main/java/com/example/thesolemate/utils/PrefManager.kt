package com.example.thesolemate.utils


import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences("TheSoleMatePrefs", Context.MODE_PRIVATE)

    fun saveUser(username: String, name: String, email: String) {
        with(pref.edit()) {
            putString("username", username)
            putString("name", name)
            putString("email", email)
            apply()
        }
    }

    fun getUsername(): String? = pref.getString("username", null)
    fun getName(): String? = pref.getString("name", null)
    fun getEmail(): String? = pref.getString("email", null)

    fun isLoggedIn(): Boolean = getUsername() != null

    fun logout() {
        with(pref.edit()) {
            clear()
            apply()
        }
    }
}
