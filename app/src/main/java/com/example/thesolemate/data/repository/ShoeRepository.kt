package com.example.thesolemate.data.repository

import com.example.thesolemate.data.remote.ApiService
import com.example.thesolemate.model.request.ShoeRequest
import com.example.thesolemate.model.response.ShoeResponse
import retrofit2.Response

class ShoeRepository(private val apiService: ApiService) {

    suspend fun getShoes(): Response<List<ShoeResponse>> {
        return apiService.getShoes()
    }

    suspend fun getShoeById(id: Int): Response<ShoeResponse> {
        return apiService.getShoeById(id)
    }


}
