package com.example.travelassistant

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao
{
    @Query("SELECT * FROM location")
    fun getAll(): LiveData<List<LocationModel>>

    @Insert
    fun insertAll(locationModels: List<LocationModel>)

    @Query("DELETE FROM location")
    fun deleteAll()
}