package com.example.travelassistant

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationModel(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageURL: String?
)
{
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}