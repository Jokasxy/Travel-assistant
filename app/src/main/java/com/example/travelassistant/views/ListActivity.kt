package com.example.travelassistant.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelassistant.R
import com.example.travelassistant.adapters.LocationsListAdapter
import com.example.travelassistant.models.LocationModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.view.*
import java.lang.reflect.Type

class ListActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        sharedPreferences = getSharedPreferences(getString(R.string.favorite_locations), Context.MODE_PRIVATE)

        val locationsType: Type = object : TypeToken<MutableList<LocationModel>>() {}.getType()
        val locations: MutableList<LocationModel> = Gson().fromJson(intent.getStringExtra("locations"), locationsType)

        viewManager = LinearLayoutManager(this)
        viewAdapter = LocationsListAdapter(locations)

        recyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        if (locations.size == 0) {
            noFavorite.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_favorite -> {
            val locationsType: Type = object : TypeToken<MutableList<LocationModel>>() {}.getType()
            val favoriteLocations: MutableList<LocationModel> =
                Gson().fromJson(sharedPreferences.getString(getString(R.string.favorite_locations), String()), locationsType)
            val intent = Intent(this, ListActivity::class.java).apply {
                putExtra("locations", Gson().toJson(favoriteLocations))
            }
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}