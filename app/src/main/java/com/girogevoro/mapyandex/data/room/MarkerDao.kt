package com.girogevoro.mapyandex.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.girogevoro.mapyandex.domain.entity.MarkerEntity

@Dao
interface MarkerDao {

    @Query("SELECT * FROM Marker")
    suspend fun getMarker(): List<MarkerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(markerEntity: MarkerEntity): Long

    @Update
    suspend fun updateMarker(markerEntityList: MarkerEntity)

    @Update
    suspend fun updateMarker(markerEntityList: List<MarkerEntity>)

    @Delete
    suspend fun deleteMarker(markerEntity: MarkerEntity)

}