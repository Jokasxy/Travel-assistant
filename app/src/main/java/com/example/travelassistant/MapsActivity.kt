package com.example.travelassistant

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rma5_maps.hasPermissionCompat
import com.example.rma5_maps.requestPermissionCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback
{

    private lateinit var map: GoogleMap
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val locationRequestCode = 10
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder
    private lateinit var locationViewModel: LocationViewModel

    private val locationListener = object: LocationListener
    {
        override fun onProviderEnabled(provider: String?) { }
        override fun onProviderDisabled(provider: String?) { }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
        override fun onLocationChanged(location: Location?)
        {
            updateLocationDisplay(location)
        }
    }

    private fun updateLocationDisplay(location: Location?)
    {
        if (location != null)
        {
            val modifiedLocation = LatLng(location.latitude, location.longitude)
            val geoLocation = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            map.addMarker(MarkerOptions().position(modifiedLocation).title(geoLocation[0].getAddressLine(0)))
            map.moveCamera(CameraUpdateFactory.newLatLng(modifiedLocation))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        geocoder = Geocoder(this)

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        map = googleMap
        trackLocation()
    }

    private fun trackLocation()
    {
        if(hasPermissionCompat(locationPermission))
        {
            startTrackingLocation()
        }
        else
        {
            requestPermissionCompat(arrayOf(locationPermission), locationRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when(requestCode)
        {
            locationRequestCode ->
            {
                if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    trackLocation()
                else
                    Toast.makeText(this, R.string.permissionNotGranted, Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    @SuppressLint("MissingPermission")
    private fun startTrackingLocation()
    {
        Log.d("TAG", "Tracking location")
        val criteria: Criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L
        val minDistance = 10.0F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            if (locationManager.isLocationEnabled)
            {
                locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
            }
            else
            {
                Toast.makeText(this, R.string.locationDisabled, Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            try
            {
                locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
            }
            catch (e: Exception)
            {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause()
    {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }
}