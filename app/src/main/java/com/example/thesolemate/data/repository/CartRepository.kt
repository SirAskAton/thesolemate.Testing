package com.example.thesolemate.repository

import com.example.thesolemate.data.remote.ApiService
import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.request.UserIdRequest
import com.example.thesolemate.model.response.CartActionResponse
import com.example.thesolemate.model.response.CartItemResponse
import retrofit2.Response

class CartRepository(private val apiService: ApiService) {

    // Mendapatkan data cart berdasarkan userId
    suspend fun getCart(userId: Int): List<CartItemResponse> {
        val response = apiService.getCart(UserIdRequest(userId))
        if (response.isSuccessful) {
            return (response.body() ?: emptyList()) as List<CartItemResponse>
        } else {
            throw Exception("Gagal memuat cart: ${response.code()}")
        }
    }

    // Menambahkan item ke cart
    suspend fun addToCart(request: CartRequest): Response<CartActionResponse> {
        return apiService.addToCart(request)
    }

    // Memperbarui item cart berdasarkan ID
    suspend fun updateCartItem(cartId: Int, request: CartRequest): Response<CartActionResponse> {
        return apiService.updateCart(cartId, request)
    }

    // Menghapus item dari cart
    suspend fun removeFromCart(cartId: Int): Response<CartActionResponse> {
        return apiService.removeFromCart(cartId)
    }

    // Melakukan checkout untuk user
    suspend fun checkout(userId: Int): Response<CartActionResponse> {
        return apiService.checkout(mapOf("user_id" to userId))
    }
}
