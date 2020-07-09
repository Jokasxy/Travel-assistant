package com.example.travelassistant

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [LocationModel::class], version = 1)
abstract class DatabaseService : RoomDatabase()
{
    abstract fun locationDao(): LocationDao

    private class DatabaseServiceCallback(private val scope: CoroutineScope) : RoomDatabase.Callback()
    {
        override fun onOpen(db: SupportSQLiteDatabase)
        {
            super.onOpen(db)
            instance?.let { database ->
                scope.launch {
                    var dao = database.locationDao()
                    dao.deleteAll()

                    dao.insertAll(listOf(
                        LocationModel("Plitvice lakes", 44.88126, 15.62275,
                        "Plitvice Lakes National Park is one of the oldest and largest national parks in Croatia. In 1979, Plitvice Lakes National Park was added to the UNESCO World Heritage register. The national park was founded in 1949 and is in the mountainous karst area of central Croatia, at the border to Bosnia and Herzegovina. The important north–south road that passes through the national park area connects the Croatian inland with the Adriatic coastal region. The protected area extends over 296.85 square kilometres (73,350 acres). About 90% of this area is part of Lika-Senj County, while the remaining 10% is part of Karlovac County. Each year, more than 1 million visitors are recorded.  Entrance is subject to variable charges, up to 250 kuna or around €34 per adult per day in summer 2018.",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Plitvice_lakes.JPG/800px-Plitvice_lakes.JPG"),
                        LocationModel("Prague", 50.08804, 14.42076,
                        "Prague is the capital and largest city in the Czech Republic, the 13th largest city in the European Union and the historical capital of Bohemia. Situated on the Vltava river, Prague is home to about 1.3 million people, while its metropolitan area is estimated to have a population of 2.7 million. The city has a temperate oceanic climate, with relatively warm summers and chilly winters.",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/St_Vitus_Prague_September_2016-30b.jpg/800px-St_Vitus_Prague_September_2016-30b.jpg")
                    ))
                }
            }
        }
    }

    companion object
    {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var instance: DatabaseService? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DatabaseService
        {
                return instance ?: synchronized(this)
                {
                    val newInstance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseService::class.java,
                        "location_database"
                    ).addCallback(DatabaseServiceCallback(scope)).build()
                    instance = newInstance
                    return newInstance
                }
        }
    }
}