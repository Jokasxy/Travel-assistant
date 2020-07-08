package com.example.travelassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application)
{
    private val repository: LocationRepository
    val locations: LiveData<List<LocationModel>>

    init
    {
        val dao = DatabaseService.getDatabase(application).locationDao()
        repository = LocationRepository(dao)
        locations = repository.locations
    }

    fun insertAll(locations: List<LocationModel>)
    {
        viewModelScope.launch {
            repository.insertAll(locations)
        }
    }
}