package com.example.travelassistant.views

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.travelassistant.R
import com.example.travelassistant.models.LocationModel
import com.example.travelassistant.models.ImageFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_location.*


class LocationActivity : FragmentActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val location = Gson().fromJson(intent.getStringExtra("location"), LocationModel::class.java)

        name.text = location.name
        country.text = "Country: ${location.country}"
        description.text = location.description

        viewPager.adapter = PagerAdapter(supportFragmentManager, location.imageURLs)
        dots.setViewPager(viewPager)
    }

    private class PagerAdapter(fragmentManager: FragmentManager, imageUrls: List<String>?) : FragmentStatePagerAdapter(fragmentManager) {
        private val imageURLs = imageUrls

        override fun getCount(): Int = imageURLs?.size ?: 0
        override fun getItem(position: Int): Fragment = ImageFragment(imageURLs?.get(position))
    }
}