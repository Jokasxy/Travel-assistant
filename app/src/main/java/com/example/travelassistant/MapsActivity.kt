package com.example.travelassistant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.rma5_maps.hasPermissionCompat
import com.example.rma5_maps.requestPermissionCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback
{

    private lateinit var map: GoogleMap
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val locationRequestCode = 10
    private lateinit var locationManager: LocationManager

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
            map.addMarker(MarkerOptions().position(modifiedLocation).title("Current location"))
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
    private fun startTrackingLocation()
    {
        Log.d("TAG", "Tracking location")
        val criteria: Criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L
        val minDistance = 10.0F
        try
        {
            locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
        }
        catch (e: SecurityException)
        {
            Toast.makeText(this, R.string.permissionNotGranted, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause()
    {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }
}