package com.example.thesolemate.repository

import com.example.thesolemate.data.remote.ApiService
import com.example.thesolemate.model.request.*
import com.example.thesolemate.model.response.CartActionResponse
import com.example.thesolemate.model.response.CartItemResponse
import retrofit2.Response

class CartRepository(private val apiService: ApiService) {

    // Mendapatkan data cart berdasarkan userId
    suspend fun getCart(userId: Int): List<CartItemResponse> {
        val response = apiService.getCart(UserIdRequest(userId))
        val body = response.body()
        if (response.isSuccessful && body?.success == true) {
            return body.data ?: emptyList()
        } else {
            throw Exception("Gagal memuat cart: ${response.code()} - ${body?.message}")
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

    // ✅ Memperbarui quantity item
    suspend fun updateCartQuantity(cartId: Int, quantity: Int): Response<CartActionResponse> {
        val request = UpdateCartRequest(cart_id = cartId, quantity = quantity)
        return apiService.updateCartQuantity(request)
    }

    // ✅ Menghapus item dari cart
    suspend fun deleteCartItem(cartId: Int): Response<CartActionResponse> {
        val request = DeleteCartRequest(cart_id = cartId)
        return apiService.deleteCartItem(request)
    }

    // Melakukan checkout
    suspend fun checkout(userId: Int): Response<CartActionResponse> {
        val body = mapOf("user_id" to userId)
        return apiService.checkout(body)
    }
}
