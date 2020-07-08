package com.example.travelassistant

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationModel::class], version = 1)
abstract class DatabaseService : RoomDatabase()
{
    abstract fun locationDao(): LocationDao

    companion object
    {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var instance: DatabaseService? = null

        fun getDatabase(context: Context): DatabaseService
        {
                return instance ?: synchronized(this)
                {
                    val newInstance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseService::class.java,
                        "location_database"
                    ).build()
                    instance = newInstance
                    return newInstance
                }
        }
    }
}