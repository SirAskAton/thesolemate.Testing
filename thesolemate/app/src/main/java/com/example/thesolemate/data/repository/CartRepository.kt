package com.example.thesolemate.data.repository

import com.example.thesolemate.data.remote.ApiService
import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.response.CartResponse
import com.example.thesolemate.model.response.CheckoutResponse
import retrofit2.Response

class CartRepository(private val api: ApiService) {

    suspend fun getCartItems(): Response<List<CartResponse>> {
        return api.getCart()
    }

    suspend fun addToCart(shoeId: Int, quantity: Int): Boolean {
        val request = CartRequest(shoeId = shoeId, quantity = quantity)
        val response = api.addToCart(request)
        return response.isSuccessful
    }

    suspend fun updateCartItem(cartId: Int, quantity: Int): Boolean {
        val request = CartRequest(shoeId = 0, quantity = quantity) // shoeId = 0 jika backend tidak peduli
        val response = api.updateCart(cartId, request)
        return response.isSuccessful
    }

    suspend fun deleteCartItem(cartId: Int): Boolean {
        val response = api.removeFromCart(cartId)
        return response.isSuccessful
    }

    suspend fun clearCart(): Boolean {
        val response = api.checkout()
        return response.isSuccessful
    }
    suspend fun checkout(): Response<CheckoutResponse> {
        return api.checkout()
    }

}
