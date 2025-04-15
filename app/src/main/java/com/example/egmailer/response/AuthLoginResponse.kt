package com.example.egmailer.response

class AuthLoginResponse {
    private val id: String = ""
    private val name: String = ""
    private val email: String = ""
    private val token: String = ""
    private val message: String = ""

    fun getId(): String {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getToken(): String {
        return token
    }

    fun getMessage(): String {
        return message
    }
}