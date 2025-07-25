package com.example.thesolemate.model.request

data class UpdateCartRequest(
    val cart_id: Int,
    val quantity: Int
)
