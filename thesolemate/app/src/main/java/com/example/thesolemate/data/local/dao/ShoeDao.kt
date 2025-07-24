package com.example.thesolemate.data.local.dao

import androidx.room.*
import com.example.thesolemate.data.local.entity.ShoeEntity

@Dao
interface ShoeDao {
    @Query("SELECT * FROM shoes")
    suspend fun getAll(): List<ShoeEntity>

    @Query("SELECT * FROM shoes WHERE id = :id")
    suspend fun getById(id: Int): ShoeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoe: ShoeEntity)

    @Update
    suspend fun update(shoe: ShoeEntity)

    @Delete
    suspend fun delete(shoe: ShoeEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(shoes: List<ShoeEntity>)
}
