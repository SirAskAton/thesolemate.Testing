package com.example.thesolemate.model.response

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user_id: Int,
    val name: String
)
