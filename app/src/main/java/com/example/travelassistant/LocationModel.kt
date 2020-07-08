package com.example.travelassistant

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URL

@Entity(tableName = "location")
data class LocationModel(
    @PrimaryKey val id: Int,
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val description: String?,
    val imageURL: URL?
)