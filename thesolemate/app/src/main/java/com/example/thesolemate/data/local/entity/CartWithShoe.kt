package com.example.thesolemate.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartWithShoe(
    @Embedded val cart: CartEntity,

    @Relation(
        parentColumn = "shoeId",
        entityColumn = "id"
    )
    val shoe: ShoeEntity
)
