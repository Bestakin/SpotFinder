package com.example.spotfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) // Use autoGenerate for the ID
    val id: Int = 0,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
