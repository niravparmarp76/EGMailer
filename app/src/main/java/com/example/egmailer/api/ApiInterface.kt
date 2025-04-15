package com.example.egmailer.api

import com.example.egmailer.request.AuthLoginRequest
import com.example.egmailer.response.AuthLoginResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/api/v1/auth/login")
    fun authLogin(
        @Body request: AuthLoginRequest
    ): Observable<AuthLoginResponse>
}