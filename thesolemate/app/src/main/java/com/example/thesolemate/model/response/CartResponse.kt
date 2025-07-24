package com.example.thesolemate.model.response

data class CartResponse(
    val id: Int,                 // ✅ Harus ada!
    val shoeId: Int,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int

)
