package com.example.thesolemate.model.response


class ItemResponse {
}

data class CartItem(
    val id: String,
    val shoe: Shoe,
    val quantity: Int
)

data class Shoe(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String
)