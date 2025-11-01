package com.example.spotfinder.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations WHERE address LIKE '%' || :query || '%' LIMIT 1")
    suspend fun findLocation(query: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(location: LocationEntity)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("SELECT * FROM locations ORDER BY id DESC")
    suspend fun getAllLocations(): List<LocationEntity>
}