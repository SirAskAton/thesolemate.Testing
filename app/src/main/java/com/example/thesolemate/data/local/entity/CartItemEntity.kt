package com.example.thesolemate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shoeId: Int,
    val name: String,
    val size: String,
    val quantity: Int,
    val price: Int
)
