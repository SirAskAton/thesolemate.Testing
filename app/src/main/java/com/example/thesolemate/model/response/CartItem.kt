package com.example.thesolemate.model.response

data class CartItem(
    val id: String,
    val shoe: Shoe,
    val quantity: Int
)