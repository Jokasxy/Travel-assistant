package com.example.travelassistant.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.travelassistant.R
import com.example.travelassistant.models.LocationModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_location.*
import java.lang.reflect.Type


class LocationActivity : FragmentActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favoriteLocations: MutableList<LocationModel>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val location: LocationModel = Gson().fromJson(intent.getStringExtra("location"), LocationModel::class.java)

        name.text = location.name
        country.text = "Country: ${location.country}"
        description.text = location.description

        viewPager.adapter = PagerAdapter(supportFragmentManager, location.imageURLs)
        dots.setViewPager(viewPager)

        val locationsType: Type = object : TypeToken<MutableList<LocationModel>>() {}.getType()
        sharedPreferences = getSharedPreferences(getString(R.string.favorite_locations), Context.MODE_PRIVATE)

        favoriteLocations = Gson().fromJson(sharedPreferences.getString(getString(R.string.favorite_locations), "[]"), locationsType)

        if(favoriteLocations.contains(location)) {
            favoriteButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorGold)
        }

        favoriteButton.setOnClickListener {
            if(favoriteLocations.contains(location)) {
                favoriteButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
                Toast.makeText(this, getString(R.string.favorite_removed), Toast.LENGTH_LONG).show()
                favoriteLocations.remove(location)
            }
            else {
                favoriteButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorGold)
                Toast.makeText(this, getString(R.string.favorite_added), Toast.LENGTH_LONG).show()
                favoriteLocations.add(location)
            }
            sharedPreferences.edit().putString(getString(R.string.favorite_locations), Gson().toJson(favoriteLocations)).apply()
        }
    }

    private class PagerAdapter(fragmentManager: FragmentManager, imageUrls: List<String>?) : FragmentStatePagerAdapter(fragmentManager) {
        private val imageURLs = imageUrls

        override fun getCount(): Int = imageURLs?.size ?: 0
        override fun getItem(position: Int): Fragment = ImageFragment(imageURLs?.get(position))
    }
}