package com.example.thesolemate.data.remote

import com.example.thesolemate.model.request.*
import com.example.thesolemate.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------------- AUTH ----------------

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ---------------- SHOES ----------------

    @GET("shoes/get_all.php")
    suspend fun getShoes(): Response<List<ShoeResponse>>

    @GET("get_shoe.php")
    suspend fun getShoeById(@Query("id") id: Int): Response<ShoeResponse>

    @POST("thesolemate_api/shoes/create.php")
    suspend fun createShoe(@Body request: ShoeRequest): Response<ShoeResponse>

    @PUT("thesolemate_api/shoes/update.php")
    suspend fun updateShoe(
        @Query("id") id: Int,
        @Body request: ShoeRequest
    ): Response<ShoeResponse>

    @DELETE("thesolemate_api/shoes/delete.php")
    suspend fun deleteShoe(@Query("id") id: Int): Response<Unit>

    // ---------------- CART ----------------

    @POST("thesolemate_api/cart/get.php")
    suspend fun getCart(@Body request: UserIdRequest): Response<List<CartItemResponse>>


    annotation class CartItemResponse

    @POST("thesolemate_api/cart/add.php")
    suspend fun addToCart(@Body request: CartRequest): Response<CartActionResponse>

    @PUT("thesolemate_api/cart/update.php")
    suspend fun updateCart(
        @Query("id") cartId: Int,
        @Body request: CartRequest
    ): Response<CartActionResponse>

    @DELETE("thesolemate_api/cart/delete.php")
    suspend fun removeFromCart(@Query("id") cartId: Int): Response<CartActionResponse>

    @POST("checkout.php")
    suspend fun checkout(@Body request: Map<String, Int>): Response<CartActionResponse>
}
