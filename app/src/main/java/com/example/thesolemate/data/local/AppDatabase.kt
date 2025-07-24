package com.example.thesolemate.data.local

import CartDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thesolemate.data.local.dao.UserDao
import com.example.thesolemate.data.local.entity.CartItemEntity
import com.example.thesolemate.data.local.entity.UserEntity

@Database(entities = [CartItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solemate_db"
                ).build().also { INSTANCE = it }
            }
    }
}
