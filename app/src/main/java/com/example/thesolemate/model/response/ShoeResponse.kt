package com.example.thesolemate.model.response

data class ShoeResponse(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Int,
    val gender: String,
    val image_url: String,
    val description: String
)
