package com.example.thesolemate.model.request

data class CartRequest(
    val user_id: Int,
    val shoe_id: Int,
    val quantity: Int = 1
)
