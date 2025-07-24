package com.example.thesolemate.data.remote

import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.request.LoginRequest
import com.example.thesolemate.model.request.RegisterRequest
import com.example.thesolemate.model.request.ShoeRequest
import com.example.thesolemate.model.response.CartResponse
import com.example.thesolemate.model.response.CheckoutResponse
import com.example.thesolemate.model.response.LoginResponse
import com.example.thesolemate.model.response.RegisterResponse
import com.example.thesolemate.model.response.ShoeResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------------- AUTH ----------------
    @GET("user")
    fun loginWithQuery(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<List<RegisterResponse>>

    @GET("user")
    fun getAllUsers(): Call<List<RegisterResponse>>

    @POST("user")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    // ---------------- SHOES ----------------
    @GET("shoes")
    suspend fun getShoes(): Response<List<ShoeResponse>>

    @GET("shoes/{id}")
    suspend fun getShoeById(@Path("id") id: Int): Response<ShoeResponse>

    @POST("shoes")
    suspend fun createShoe(@Body request: ShoeRequest): Response<ShoeResponse>

    @PUT("shoes/{id}")
    suspend fun updateShoe(@Path("id") id: Int, @Body request: ShoeRequest): Response<ShoeResponse>

    @DELETE("shoes/{id}")
    suspend fun deleteShoe(@Path("id") id: Int): Response<Unit>


    // ---------------- CART ----------------
    @GET("cart")
    suspend fun getCart(): Response<List<CartResponse>>

    @POST("cart")
    suspend fun addToCart(@Body request: CartRequest): Response<CartResponse>

    @PUT("cart/{id}")
    suspend fun updateCart(@Path("id") id: Int, @Body request: CartRequest): Response<CartResponse>

    @DELETE("cart/{id}")
    suspend fun removeFromCart(@Path("id") id: Int): Response<Unit>

    @DELETE("cart")
    suspend fun clearCart(): Response<Unit>


    // ---------------- CHECKOUT ----------------
    @POST("checkout")
    suspend fun checkout(): Response<CheckoutResponse>
}
