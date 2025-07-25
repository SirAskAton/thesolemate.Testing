package com.example.thesolemate.data.remote

import com.example.thesolemate.model.request.*
import com.example.thesolemate.model.response.*
import retrofit2.Call
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

    @POST("shoes/create.php")
    suspend fun createShoe(@Body request: ShoeRequest): Response<ShoeResponse>

    @PUT("shoes/update.php")
    suspend fun updateShoe(
        @Query("id") id: Int,
        @Body request: ShoeRequest
    ): Response<ShoeResponse>
    @GET("get_shoe_by_id.php")
    suspend fun getShoeById(@Query("id") id: Int): Response<ShoeResponse>

    @DELETE("shoes/delete.php")
    suspend fun deleteShoe(@Query("id") id: Int): Response<Unit>

    // ---------------- CART ----------------

    @POST("get_cart.php")
    suspend fun getCart(@Body request: UserIdRequest): Response<CartListResponse>

    @POST("cart/checkout.php")
    suspend fun checkout(@Body request: Map<String, Int>): Response<CartActionResponse>

    @POST("cart/add.php")
    suspend fun addToCart(@Body request: CartRequest): Response<CartActionResponse>

    @PUT("cart/update.php")
    suspend fun updateCart(
        @Query("id") cartId: Int,
        @Body request: CartRequest
    ): Response<CartActionResponse>


    @POST("cart/update_quantity.php")
    suspend fun updateCartQuantity(
        @Body request: UpdateCartRequest
    ): Response<CartActionResponse>   // ✅ pakai suspend + Response

    @POST("cart/delete_item.php")
    suspend fun deleteCartItem(
        @Body request: DeleteCartRequest
    ): Response<CartActionResponse>   // ✅ pakai suspend + Response

    @GET("cart/get_cart.php")
    suspend fun getCart(
        @Query("user_id") userId: Int
    ): Response<CartListResponse>


}
