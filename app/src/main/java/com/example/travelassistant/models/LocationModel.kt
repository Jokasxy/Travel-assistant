package com.example.travelassistant.models

import com.google.android.gms.maps.model.LatLng

data class LocationModel (
    val name: String?,
    val country: String?,
    val position: LatLng?,
    val description: String?,
    val imageURL: String?
)