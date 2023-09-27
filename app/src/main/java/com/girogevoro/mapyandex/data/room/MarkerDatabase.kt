package com.girogevoro.mapyandex.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.girogevoro.mapyandex.domain.entity.MarkerEntity

@Database(entities = [MarkerEntity::class], version = 1, exportSchema = false)
abstract class MarkerDatabase : RoomDatabase() {
    abstract fun getMarketDao(): MarkerDao
}