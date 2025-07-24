package com.example.thesolemate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shoeId: Int,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int
)
