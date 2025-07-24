package com.example.thesolemate.model.response

data class CheckoutResponse(
    val items: List<CartItem>,
    val totalPrice: Double
)

