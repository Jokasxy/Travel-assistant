package com.example.travelassistant

import androidx.lifecycle.LiveData

class LocationRepository(private val locationDao: LocationDao)
{
    val locations: LiveData<List<LocationModel>> = locationDao.getAll()
}