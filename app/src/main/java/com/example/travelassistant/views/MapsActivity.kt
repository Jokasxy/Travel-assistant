package com.example.travelassistant.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.travelassistant.R
import com.example.travelassistant.utils.hasPermissionCompat
import com.example.travelassistant.utils.requestPermissionCompat
import com.example.travelassistant.models.LocationModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception
import java.lang.reflect.Type

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnInfoWindowLongClickListener {

    private lateinit var map: GoogleMap
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val locationRequestCode = 10
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder
    private val db = Firebase.firestore
    private val locations: MutableList<LocationModel> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences

    private val locationListener = object: LocationListener {
        override fun onProviderEnabled(provider: String?) { }
        override fun onProviderDisabled(provider: String?) { }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
        override fun onLocationChanged(location: Location?) {
            updateLocationDisplay(location)
        }
    }

    private fun updateLocationDisplay(location: Location?) {
        if (location != null) {
            val modifiedLocation = LatLng(location.latitude, location.longitude)
            val geoLocation = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            map.addMarker(MarkerOptions().position(modifiedLocation).title(geoLocation[0].getAddressLine(0)).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            ))
            map.moveCamera(CameraUpdateFactory.newLatLng(modifiedLocation))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        geocoder = Geocoder(this)

        sharedPreferences = getSharedPreferences(getString(R.string.favorite_locations), Context.MODE_PRIVATE)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        trackLocation()

        setLocations()
        map.setOnInfoWindowLongClickListener(this)

        fab.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java).apply {
                putExtra("locations", Gson().toJson(locations))
            }
            startActivity(intent)
        }
    }

    private fun trackLocation() {
        if(hasPermissionCompat(locationPermission)) {
            startTrackingLocation()
        }
        else {
            requestPermissionCompat(arrayOf(locationPermission), locationRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            locationRequestCode -> {
                if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    trackLocation()
                else
                    Toast.makeText(this, R.string.permissionNotGranted, Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    @SuppressLint("MissingPermission")
    private fun startTrackingLocation() {
        val criteria: Criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L
        val minDistance = 10.0F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (locationManager.isLocationEnabled) {
                locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
            }
            else {
                Toast.makeText(this, R.string.locationDisabled, Toast.LENGTH_SHORT).show()
            }
        }
        else {
            try {
                locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
            }
            catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    private fun setLocations() {
        db.collection("locations").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val position = document.getGeoPoint("position")
                    val location = LocationModel(
                        document.getString("name"),
                        document.getString("country"),
                        position?.latitude?.let { LatLng(it, position.longitude) },
                        document.getString("description"),
                        document.data.getValue("imageURLs") as List<String>?
                    )
                    locations.add(location)
                    val marker = map.addMarker(location.position?.let { MarkerOptions().position(it).title(location.name).snippet(location.country) })
                    marker.tag = location
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onInfoWindowLongClick(marker: Marker) {
        val intent = Intent(this, LocationActivity::class.java).apply {
            putExtra("location", Gson().toJson(marker.tag))
        }
        startActivity(intent)
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