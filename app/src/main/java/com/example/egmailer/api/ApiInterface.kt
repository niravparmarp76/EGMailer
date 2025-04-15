package com.example.egmailer.api

import com.example.egmailer.request.AuthLoginRequest
import com.example.egmailer.request.AuthRegisterRequest
import com.example.egmailer.response.AuthLoginResponse
import com.example.egmailer.response.AuthRegisterResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/api/v1/auth/login")
    fun authLogin(
        @Body request: AuthLoginRequest
    ): Observable<AuthLoginResponse>

    @POST("/api/v1/auth/register")
    fun authRegister(
        @Body request: AuthRegisterRequest
    ): Observable<AuthRegisterResponse>
}