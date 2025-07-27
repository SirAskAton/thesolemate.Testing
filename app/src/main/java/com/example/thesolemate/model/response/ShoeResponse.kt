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
{
    val fullImageUrl: String
        get() = "http://10.0.2.2/thesolemate_api/Shoes/$image_url"
}