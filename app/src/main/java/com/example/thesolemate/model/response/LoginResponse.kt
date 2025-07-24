package com.example.thesolemate.model.response

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user: User?
)

data class User(
    val id: Int,
    val username: String,
    val email: String
)
