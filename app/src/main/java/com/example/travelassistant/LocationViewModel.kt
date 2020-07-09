package com.example.travelassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

class LocationViewModel(application: Application) : AndroidViewModel(application)
{
    private val repository: LocationRepository
    val locations: LiveData<List<LocationModel>>

    init
    {
        val dao = DatabaseService.getDatabase(application, viewModelScope).locationDao()
        repository = LocationRepository(dao)
        locations = repository.locations
    }
}