package com.example.egmailer.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("EGMailerPref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val tokenKey = "user_token"
    private val nameKey = "user_name"
    private val emailKey = "user_email"

    fun saveUserSession(token: String, name: String, email: String) {
        editor.apply {
            putString(tokenKey, token)
            putString(nameKey, name)
            putString(emailKey, email)
            apply()
        }
    }

    fun getUserName(): String? = sharedPref.getString(nameKey, null)
    fun getUserEmail(): String? = sharedPref.getString(emailKey, null)
    fun getUserToken(): String? = sharedPref.getString(tokenKey, null)

    fun clearSession() {
        editor.clear().apply()
    }
}
