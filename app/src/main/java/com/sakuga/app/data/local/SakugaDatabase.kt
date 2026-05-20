package com.sakuga.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sakuga.app.data.local.dao.FavoriteDao
import com.sakuga.app.data.local.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SakugaDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
