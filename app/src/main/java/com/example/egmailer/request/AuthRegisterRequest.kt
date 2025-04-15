package com.example.egmailer.request

data class AuthRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)