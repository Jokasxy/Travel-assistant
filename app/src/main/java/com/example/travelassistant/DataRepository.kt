package com.example.travelassistant

import androidx.lifecycle.LiveData

class DataRepository(private val locationDao: LocationDao)
{
    val locations: LiveData<List<LocationModel>> = locationDao.getAll()

    fun insertAll(locationModels: List<LocationModel>)
    {
        locationDao.insertAll(locationModels)
    }
}