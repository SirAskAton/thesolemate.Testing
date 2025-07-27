package com.example.thesolemate.model.response

import java.io.Serializable

data class CartItemResponse(
    val id: Int,
    val shoe_id: Int,
    val shoe_name: String,
    val image_url: String,
    val price: Int,
    val quantity: Int,
    val cart_id: Int
) : Serializable
