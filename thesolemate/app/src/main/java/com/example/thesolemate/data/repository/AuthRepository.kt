package com.example.thesolemate.data.repository

import com.example.thesolemate.data.remote.ApiService
import com.example.thesolemate.model.request.LoginRequest
import com.example.thesolemate.model.request.RegisterRequest
import com.example.thesolemate.model.response.LoginResponse
import com.example.thesolemate.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    fun login(username: String, password: String): Call<List<RegisterResponse>> {
        return apiService.loginWithQuery(username, password)
    }


    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }
}
