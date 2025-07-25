package com.example.thesolemate.model.response

data class CartListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<CartItemResponse>
)
