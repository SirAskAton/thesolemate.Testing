package com.example.thesolemate.model.response


data class LoginResponse(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
    val password: String
)

