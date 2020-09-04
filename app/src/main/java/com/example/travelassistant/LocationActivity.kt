package com.example.travelassistant

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelassistant.models.LocationModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_location.*


class LocationActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val location = Gson().fromJson(intent.getStringExtra("location"), LocationModel::class.java)

        Log.d("TAG", location.toString())

        name.text = location.name
        country.text = "Country: ${location.country}"
        Glide.with(this).load(location.imageURL).into(images)
        description.text = location.description
    }
}