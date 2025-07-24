package com.example.thesolemate.model.response

data class CartItemResponse(
    val id: Int,
    val userId: Int,
    val shoeId: Int,
    val name: String,
    val image: String,
    val price: Int,
    val quantity: Int
)
