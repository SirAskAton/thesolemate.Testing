package com.example.thesolemate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoes")
data class ShoeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val description: String?
)
